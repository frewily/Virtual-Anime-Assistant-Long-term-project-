package com.assistant.service;

import com.assistant.config.VoiceConfig;
import com.assistant.model.TTSRequest;
import com.assistant.model.TTSResponse;
import com.assistant.model.VoiceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TTSService {

    private final VoiceConfig voiceConfig;
    private final HttpClient httpClient;
    private final Map<String, byte[]> audioCache = new ConcurrentHashMap<>();
    private final Map<String, String> audioFormatCache = new ConcurrentHashMap<>();

    @Value("${assistant.gpt-sovits.api-url:http://localhost:9880}")
    private String gptSovitsUrl;

    @Value("${assistant.gpt-sovits.timeout:30000}")
    private int gptSovitsTimeout;

    public TTSService(VoiceConfig voiceConfig) {
        this.voiceConfig = voiceConfig;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public TTSResponse synthesize(TTSRequest request) {
        String voiceId = request.getVoiceId();
        String fallbackVoice = request.getFallbackVoice();

        if (voiceId == null || voiceId.isEmpty()) {
            VoiceInfo defaultVoice = voiceConfig.getDefaultVoice();
            voiceId = defaultVoice != null ? defaultVoice.getId() : null;
        }
        if (fallbackVoice == null || fallbackVoice.isEmpty()) {
            fallbackVoice = voiceConfig.getDefaultFallbackVoice();
        }

        TTSAttempt result = tryGptSovits(request.getText(), voiceId);
        if (result == null) {
            result = tryEdgeTTS(request.getText(), fallbackVoice);
        }
        if (result == null) {
            result = tryMacOSSay(request.getText());
        }

        if (result != null) {
            String audioId = UUID.randomUUID().toString();
            audioCache.put(audioId, result.audioBytes);
            audioFormatCache.put(audioId, result.format);
            return TTSResponse.success(audioId, result.format, 0, result.voice);
        }

        return TTSResponse.fail("All TTS engines failed");
    }

    public byte[] getAudioBytes(String audioId) {
        return audioCache.get(audioId);
    }

    public String getAudioFormat(String audioId) {
        return audioFormatCache.get(audioId);
    }

    private TTSAttempt tryGptSovits(String text, String voiceId) {
        try {
            VoiceInfo voice = voiceConfig.getVoice(voiceId);
            String refAudio = voice != null ? voice.getReferenceAudio() : "";
            String promptText = voice != null ? voice.getPromptText() : "";

            String jsonBody = "{"
                    + "\"text\":\"" + escapeJson(text) + "\","
                    + "\"text_language\":\"zh\","
                    + "\"prompt_text\":\"" + escapeJson(promptText) + "\","
                    + "\"prompt_language\":\"zh\","
                    + "\"refer_wav_path\":\"" + escapeJson(refAudio) + "\""
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(gptSovitsUrl + "/tts"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(gptSovitsTimeout))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200 && response.body() != null && response.body().length > 0) {
                System.out.println("GPT-SoVITS synthesized: " + text);
                return new TTSAttempt(response.body(), "wav", "gpt-sovits:" + voiceId);
            }
        } catch (Exception e) {
            System.out.println("GPT-SoVITS failed: " + e.getMessage());
        }
        return null;
    }

    private TTSAttempt tryEdgeTTS(String text, String voice) {
        try {
            Path tempFile = Files.createTempFile("tts_edge_", ".mp3");
            ProcessBuilder pb = new ProcessBuilder(
                    "/Library/Frameworks/Python.framework/Versions/3.14/bin/python3", "-m", "edge_tts",
                    "--voice", voice != null ? voice : "zh-CN-XiaoxiaoNeural",
                    "--text", text,
                    "--write-media", tempFile.toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            byte[] audioBytes = Files.readAllBytes(tempFile);
            Files.deleteIfExists(tempFile);

            if (audioBytes.length > 0) {
                System.out.println("EdgeTTS synthesized: " + text);
                return new TTSAttempt(audioBytes, "mp3", "edgetts:" + voice);
            }
        } catch (Exception e) {
            System.out.println("EdgeTTS failed: " + e.getMessage());
        }
        return null;
    }

    private TTSAttempt tryMacOSSay(String text) {
        try {
            Path tempFile = Files.createTempFile("tts_say_", ".aiff");
            String outputPath = tempFile.toAbsolutePath().toString().replace(".aiff", "");
            ProcessBuilder pb = new ProcessBuilder(
                    "say", "-v", "Tingting", "-o", outputPath, text);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (InputStream is = process.getInputStream()) {
                is.readAllBytes();
            }
            process.waitFor();

            File aiffFile = new File(outputPath + ".aiff");
            if (aiffFile.exists()) {
                Path wavFile = Files.createTempFile("tts_say_wav_", ".wav");
                ProcessBuilder convertPb = new ProcessBuilder(
                        "afconvert", "-f", "WAVE", "-d", "LEI16",
                        aiffFile.getAbsolutePath(), wavFile.toAbsolutePath().toString());
                convertPb.redirectErrorStream(true);
                Process convertProcess = convertPb.start();
                convertProcess.waitFor();
                aiffFile.delete();

                byte[] audioBytes = Files.readAllBytes(wavFile);
                wavFile.toFile().delete();

                if (audioBytes.length > 0) {
                    System.out.println("macOS say synthesized: " + text);
                    return new TTSAttempt(audioBytes, "wav", "macos-say");
                }
            }
        } catch (Exception e) {
            System.out.println("macOS say failed: " + e.getMessage());
        }
        return null;
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public List<VoiceInfo> getVoices() {
        return voiceConfig.getAllVoices();
    }

    private static class TTSAttempt {
        final byte[] audioBytes;
        final String format;
        final String voice;

        TTSAttempt(byte[] audioBytes, String format, String voice) {
            this.audioBytes = audioBytes;
            this.format = format;
            this.voice = voice;
        }
    }
}
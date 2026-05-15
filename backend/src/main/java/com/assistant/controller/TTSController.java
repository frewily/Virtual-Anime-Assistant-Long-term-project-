package com.assistant.controller;

import com.assistant.model.TTSRequest;
import com.assistant.model.TTSResponse;
import com.assistant.model.VoiceInfo;
import com.assistant.service.TTSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tts")
public class TTSController {

    private final TTSService ttsService;

    public TTSController(TTSService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping("/speak")
    public ResponseEntity<TTSResponse> speak(@RequestBody TTSRequest request) {
        if (request.getText() == null || request.getText().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(TTSResponse.fail("text is required"));
        }

        TTSResponse response = ttsService.synthesize(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audio/{audioId}")
    public ResponseEntity<byte[]> getAudio(@PathVariable String audioId) {
        byte[] audioBytes = ttsService.getAudioBytes(audioId);
        if (audioBytes == null) {
            return ResponseEntity.notFound().build();
        }

        String format = ttsService.getAudioFormat(audioId);
        String contentType = switch (format) {
            case "mp3" -> "audio/mpeg";
            case "aiff" -> "audio/aiff";
            default -> "audio/wav";
        };

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(audioBytes);
    }

    @GetMapping("/voices")
    public ResponseEntity<List<VoiceInfo>> getVoices() {
        List<VoiceInfo> voices = ttsService.getVoices();
        return ResponseEntity.ok(voices);
    }
}

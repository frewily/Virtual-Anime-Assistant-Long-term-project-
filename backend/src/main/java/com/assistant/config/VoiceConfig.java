package com.assistant.config;

import com.assistant.model.VoiceInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class VoiceConfig {

    @Value("${assistant.voice.config-path:../config/voices.yml}")
    private String configPath;

    private List<VoiceInfo> voices = new ArrayList<>();
    private String defaultVoiceId = "character_001";
    private String defaultFallbackVoice = "zh-CN-XiaoxiaoNeural";

    @PostConstruct
    public void loadVoices() {
        try (InputStream input = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> voiceList = (List<Map<String, Object>>) data.get("voices");
            if (voiceList != null) {
                for (Map<String, Object> v : voiceList) {
                    VoiceInfo info = new VoiceInfo();
                    info.setId((String) v.get("id"));
                    info.setName((String) v.get("name"));
                    info.setDescription((String) v.get("description"));
                    info.setReferenceAudio((String) v.get("referenceAudio"));
                    info.setPromptText((String) v.get("promptText"));
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) v.get("defaultParams");
                    info.setDefaultParams(params);
                    voices.add(info);
                }
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> defaults = (Map<String, Object>) data.get("default");
            if (defaults != null) {
                if (defaults.get("voiceId") != null) {
                    defaultVoiceId = (String) defaults.get("voiceId");
                }
                if (defaults.get("fallbackVoice") != null) {
                    defaultFallbackVoice = (String) defaults.get("fallbackVoice");
                }
            }

            System.out.println("Loaded " + voices.size() + " voices from " + configPath);
        } catch (Exception e) {
            System.err.println("Failed to load voices config: " + e.getMessage());
        }
    }

    public VoiceInfo getVoice(String voiceId) {
        return voices.stream()
                .filter(v -> v.getId().equals(voiceId))
                .findFirst()
                .orElse(null);
    }

    public VoiceInfo getDefaultVoice() {
        return getVoice(defaultVoiceId);
    }

    public String getDefaultFallbackVoice() {
        return defaultFallbackVoice;
    }

    public List<VoiceInfo> getAllVoices() {
        return Collections.unmodifiableList(voices);
    }
}

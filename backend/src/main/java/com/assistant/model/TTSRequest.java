package com.assistant.model;

public class TTSRequest {
    private String text;
    private String voiceId;
    private String fallbackVoice;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getFallbackVoice() {
        return fallbackVoice;
    }

    public void setFallbackVoice(String fallbackVoice) {
        this.fallbackVoice = fallbackVoice;
    }
}
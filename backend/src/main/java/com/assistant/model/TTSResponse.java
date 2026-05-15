package com.assistant.model;

public class TTSResponse {
    private boolean success;
    private String audioId;
    private String audioFormat;
    private double duration;
    private String voiceUsed;
    private String errorMessage;

    public static TTSResponse success(String audioId, String audioFormat, double duration, String voiceUsed) {
        TTSResponse response = new TTSResponse();
        response.success = true;
        response.audioId = audioId;
        response.audioFormat = audioFormat;
        response.duration = duration;
        response.voiceUsed = voiceUsed;
        return response;
    }

    public static TTSResponse fail(String errorMessage) {
        TTSResponse response = new TTSResponse();
        response.success = false;
        response.errorMessage = errorMessage;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getVoiceUsed() {
        return voiceUsed;
    }

    public void setVoiceUsed(String voiceUsed) {
        this.voiceUsed = voiceUsed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
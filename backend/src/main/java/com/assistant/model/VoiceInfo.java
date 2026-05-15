package com.assistant.model;

import java.util.List;
import java.util.Map;

public class VoiceInfo {
    private String id;
    private String name;
    private String description;
    private String referenceAudio;
    private String promptText;
    private Map<String, Object> defaultParams;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceAudio() {
        return referenceAudio;
    }

    public void setReferenceAudio(String referenceAudio) {
        this.referenceAudio = referenceAudio;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public Map<String, Object> getDefaultParams() {
        return defaultParams;
    }

    public void setDefaultParams(Map<String, Object> defaultParams) {
        this.defaultParams = defaultParams;
    }
}
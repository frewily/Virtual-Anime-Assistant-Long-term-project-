package com.assistant.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Component
public class RepliesConfig {

    @Value("${assistant.replies.config-path:../config/replies.yml}")
    private String configPath;

    private final Map<String, List<String>> categoryReplies = new HashMap<>();
    private final Map<String, String> templateReplies = new HashMap<>();
    private final Random random = new Random();

    @PostConstruct
    public void loadReplies() {
        try (InputStream input = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);

            @SuppressWarnings("unchecked")
            Map<String, Object> replies = (Map<String, Object>) data.get("replies");
            if (replies != null) {
                for (Map.Entry<String, Object> entry : replies.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) entry.getValue();
                        categoryReplies.put(entry.getKey(), list);
                    } else if (entry.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> template = (Map<String, Object>) entry.getValue();
                        if (template.containsKey("template")) {
                            templateReplies.put(entry.getKey(), (String) template.get("template"));
                        }
                    }
                }
            }
            System.out.println("Loaded " + (categoryReplies.size() + templateReplies.size()) + " reply categories");
        } catch (Exception e) {
            System.err.println("Failed to load replies config: " + e.getMessage());
        }
    }

    public String getRandomReply(String category) {
        List<String> options = categoryReplies.get(category);
        if (options != null && !options.isEmpty()) {
            return options.get(random.nextInt(options.size()));
        }
        return null;
    }

    public String getTemplateReply(String category) {
        return templateReplies.get(category);
    }

    public String formatTemplate(String category, Map<String, String> params) {
        String template = templateReplies.get(category);
        if (template == null)
            return null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
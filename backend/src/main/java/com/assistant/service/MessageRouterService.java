package com.assistant.service;

import com.assistant.config.RepliesConfig;
import com.assistant.model.ChatMessage;
import com.assistant.model.ReplyAction;
import com.assistant.model.TTSRequest;
import com.assistant.model.TTSResponse;
import com.assistant.websocket.DesktopClientHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageRouterService {

    private final RepliesConfig repliesConfig;
    private final TTSService ttsService;
    private final DesktopClientHandler desktopHandler;
    private final QQBotService qqBotService;
    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper;
    private final Map<String, String> voicePreferences = new ConcurrentHashMap<>();

    public MessageRouterService(RepliesConfig repliesConfig, TTSService ttsService,
            DesktopClientHandler desktopHandler, QQBotService qqBotService,
            DeepSeekService deepSeekService, ObjectMapper objectMapper) {
        this.repliesConfig = repliesConfig;
        this.ttsService = ttsService;
        this.desktopHandler = desktopHandler;
        this.qqBotService = qqBotService;
        this.deepSeekService = deepSeekService;
        this.objectMapper = objectMapper;
    }

    public ReplyAction routeMessage(ChatMessage message) {
        String content = message.getContent();
        if (content == null || content.isEmpty()) {
            return buildSimpleReply("unknown");
        }

        String userId = message.getSource() != null && message.getSenderId() != null
                ? message.getSource() + ":" + message.getSenderId()
                : "desktop";

        String aiReply = deepSeekService.chat(userId, content);

        String replyText;
        boolean isAi = false;
        if (aiReply != null && !aiReply.isEmpty()) {
            replyText = aiReply;
            isAi = true;
        } else {
            replyText = matchIntent(content);
        }

        ReplyAction action = new ReplyAction();
        action.setText(replyText);

        if (isAi) {
            String lower = replyText.toLowerCase();
            if (lower.contains("拜拜") || lower.contains("再见") || lower.contains("晚安")) {
                action.setExpression("idle");
                action.setMotion("wave");
            } else if (lower.contains("❤") || lower.contains("喜欢") || lower.contains("爱")) {
                action.setExpression("happy");
                action.setMotion("happy");
            } else if (lower.contains("辛苦") || lower.contains("累") || lower.contains("QAQ")) {
                action.setExpression("worried");
                action.setMotion("idle");
            } else {
                action.setExpression("happy");
                action.setMotion("wave");
            }
        } else {
            if (content.contains("你好") || content.contains("在吗") || content.contains("嗨")) {
                action.setExpression("happy");
                action.setMotion("wave");
            } else if (content.contains("再见") || content.contains("晚安")) {
                action.setExpression("idle");
                action.setMotion("wave");
            } else if (content.contains("cpu") || content.contains("CPU")
                    || content.contains("状态") || content.contains("内存")) {
                action.setExpression("curious");
                action.setMotion("idle");
            } else {
                action.setExpression("idle");
                action.setMotion("idle");
            }
        }

        action.setSendToDesktop(true);
        dispatchToDesktop(action);

        if ("qq".equals(message.getSource())) {
            qqBotService.sendReply(message, replyText);
        }

        return action;
    }

    public ReplyAction routeScenarioAction(ReplyAction action) {
        if (action == null)
            return null;
        action.setSendToDesktop(true);
        dispatchToDesktop(action);
        return action;
    }

    public void sendToDesktop(String text, String expression, String motion) {
        ReplyAction action = new ReplyAction();
        action.setText(text);
        action.setExpression(expression);
        action.setMotion(motion);
        action.setSendToDesktop(true);
        dispatchToDesktop(action);
    }

    public void updateVoicePreference(String clientId, String voice) {
        voicePreferences.put(clientId, voice);
        System.out.println("Voice preference updated: " + clientId + " -> " + voice);
    }

    private void dispatchToDesktop(ReplyAction action) {
        try {
            String audioUrl = synthesizeSpeech(action.getText());

            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("type", "speak");
            wsMessage.put("text", action.getText());
            wsMessage.put("audioUrl", audioUrl);
            wsMessage.put("expression", action.getExpression());
            wsMessage.put("motion", action.getMotion());

            String json = objectMapper.writeValueAsString(wsMessage);
            desktopHandler.broadcast(json);
            System.out.println("Dispatched to desktop: " + action.getText());
        } catch (Exception e) {
            System.err.println("Failed to dispatch to desktop: " + e.getMessage());

            Map<String, Object> fallbackMsg = new HashMap<>();
            fallbackMsg.put("type", "action");
            fallbackMsg.put("expression", action.getExpression());
            fallbackMsg.put("motion", action.getMotion());
            try {
                desktopHandler.broadcast(objectMapper.writeValueAsString(fallbackMsg));
            } catch (Exception ex) {
                System.err.println("Fallback broadcast also failed: " + ex.getMessage());
            }
        }
    }

    private String synthesizeSpeech(String text) {
        try {
            TTSRequest ttsRequest = new TTSRequest();
            ttsRequest.setText(text);
            String preferredVoice = voicePreferences.values().stream().findFirst().orElse(null);
            if (preferredVoice != null) {
                ttsRequest.setFallbackVoice(preferredVoice);
            }
            TTSResponse response = ttsService.synthesize(ttsRequest);
            if (response.isSuccess()) {
                return "http://localhost:8080/api/tts/audio/" + response.getAudioId();
            }
        } catch (Exception e) {
            System.err.println("TTS failed in router: " + e.getMessage());
        }
        return null;
    }

    private String matchIntent(String content) {
        String lower = content.toLowerCase();

        if (lower.contains("你好") || lower.contains("在吗") || lower.contains("嗨") || lower.contains("hi")) {
            return getOrRandom("greeting");
        }
        if (lower.contains("再见") || lower.contains("拜拜") || lower.contains("晚安") || lower.contains("bye")) {
            return getOrRandom("farewell");
        }
        if (lower.contains("谢谢") || lower.contains("感谢") || lower.contains("多谢")) {
            return getOrRandom("thanks");
        }
        if (lower.contains("cpu") || lower.contains("状态") || lower.contains("内存")) {
            String cpuReply = repliesConfig.getTemplateReply("status_cpu");
            String memReply = repliesConfig.getTemplateReply("status_memory");
            if (lower.contains("cpu"))
                return cpuReply != null ? cpuReply : "让我看看CPU...";
            if (lower.contains("内存"))
                return memReply != null ? memReply : "让我看看内存...";
            return "你想查看什么状态呢？CPU还是内存？";
        }
        if (lower.contains("在干嘛") || lower.contains("干嘛")) {
            return "我在看着主人工作呢~";
        }

        return getOrRandom("unknown");
    }

    private String getOrRandom(String category) {
        String reply = repliesConfig.getRandomReply(category);
        return reply != null ? reply : "嗯...";
    }

    private ReplyAction buildSimpleReply(String category) {
        ReplyAction action = new ReplyAction();
        action.setText(getOrRandom(category));
        action.setExpression("idle");
        action.setMotion("idle");
        action.setSendToDesktop(true);
        return action;
    }
}
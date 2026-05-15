package com.assistant.service;

import com.assistant.model.ChatMessage;
import com.assistant.websocket.OneBotWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class QQBotService {

    private final OneBotWebSocketHandler wsHandler;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${assistant.qq-bot.api-url:http://localhost:3000}")
    private String onebotApiUrl;

    @Value("${assistant.qq-bot.group-ids:}")
    private String groupIdsConfig;

    public QQBotService(OneBotWebSocketHandler wsHandler, ObjectMapper objectMapper) {
        this.wsHandler = wsHandler;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void sendGroupMessage(long groupId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "send_group_msg");
        payload.put("params", Map.of("group_id", groupId, "message", message));

        if (!sendViaWebSocket(payload)) {
            sendViaHttp("send_group_msg", Map.of("group_id", groupId, "message", message));
        }
    }

    public void sendPrivateMessage(long userId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "send_private_msg");
        payload.put("params", Map.of("user_id", userId, "message", message));

        if (!sendViaWebSocket(payload)) {
            sendViaHttp("send_private_msg", Map.of("user_id", userId, "message", message));
        }
    }

    public void sendReply(ChatMessage chatMsg, String replyText) {
        if (chatMsg.getGroupId() != null && !chatMsg.getGroupId().isEmpty()) {
            try {
                long groupId = Long.parseLong(chatMsg.getGroupId());
                sendGroupMessage(groupId, replyText);
            } catch (NumberFormatException e) {
                System.err.println("Invalid groupId: " + chatMsg.getGroupId());
            }
        } else {
            try {
                long userId = Long.parseLong(chatMsg.getSenderId());
                sendPrivateMessage(userId, replyText);
            } catch (NumberFormatException e) {
                System.err.println("Invalid senderId: " + chatMsg.getSenderId());
            }
        }
    }

    public boolean isConnected() {
        return wsHandler.isConnected();
    }

    private boolean sendViaWebSocket(Map<String, Object> payload) {
        WebSocketSession session = wsHandler.getBotSession();
        if (session == null || !session.isOpen())
            return false;
        try {
            String json = objectMapper.writeValueAsString(payload);
            session.sendMessage(new TextMessage(json));
            return true;
        } catch (Exception e) {
            System.err.println("WS send failed: " + e.getMessage());
            return false;
        }
    }

    private void sendViaHttp(String action, Map<String, Object> params) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("action", action);
            body.put("params", params);

            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(onebotApiUrl + "/" + action))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            System.err.println("HTTP send failed: " + response.statusCode());
                        }
                    });
        } catch (Exception e) {
            System.err.println("HTTP send failed: " + e.getMessage());
        }
    }
}
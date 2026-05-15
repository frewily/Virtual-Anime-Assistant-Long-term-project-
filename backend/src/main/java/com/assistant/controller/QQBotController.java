package com.assistant.controller;

import com.assistant.model.ChatMessage;
import com.assistant.service.MessageRouterService;
import com.assistant.service.QQBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qq")
public class QQBotController {

    private final QQBotService qqBotService;
    private final MessageRouterService routerService;

    public QQBotController(QQBotService qqBotService, MessageRouterService routerService) {
        this.qqBotService = qqBotService;
        this.routerService = routerService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestBody Map<String, Object> event) {
        String postType = (String) event.get("post_type");

        if ("message".equals(postType)) {
            String messageType = (String) event.get("message_type");
            String rawMessage = (String) event.getOrDefault("raw_message", "");
            long userId = toLong(event.get("user_id"));
            long groupId = "group".equals(messageType) ? toLong(event.get("group_id")) : 0;

            ChatMessage chatMsg = new ChatMessage();
            chatMsg.setContent(rawMessage);
            chatMsg.setSource("qq");
            chatMsg.setSenderId(String.valueOf(userId));
            chatMsg.setGroupId("group".equals(messageType) ? String.valueOf(groupId) : null);

            routerService.routeMessage(chatMsg);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", qqBotService.isConnected());
        status.put("apiUrl", "http://localhost:3000");
        status.put("wsEndpoint", "/ws/qq");
        return ResponseEntity.ok(status);
    }

    private long toLong(Object value) {
        if (value instanceof Number)
            return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
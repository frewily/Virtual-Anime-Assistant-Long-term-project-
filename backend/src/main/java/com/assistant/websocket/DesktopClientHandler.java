package com.assistant.websocket;

import com.assistant.model.ChatMessage;
import com.assistant.service.MessageRouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DesktopClientHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageRouterService routerService;

    public DesktopClientHandler(@Lazy MessageRouterService routerService) {
        this.routerService = routerService;
    }

    public int getClientCount() {
        return sessions.size();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Desktop client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Desktop client disconnected: " + session.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            String type = (String) data.get("type");

            if ("chat".equals(type)) {
                ChatMessage chatMsg = new ChatMessage();
                chatMsg.setContent((String) data.get("content"));
                chatMsg.setSource("desktop");
                chatMsg.setSenderId(session.getId());
                routerService.routeMessage(chatMsg);
            } else if ("settings".equals(type)) {
                String voice = (String) data.get("voice");
                if (voice != null) {
                    routerService.updateVoicePreference(session.getId(), voice);
                }
            } else if ("interaction".equals(type)) {
                System.out.println("Interaction from client: " + data.get("action"));
            }
        } catch (Exception e) {
            System.err.println("Failed to handle WS message: " + e.getMessage());
        }
    }

    public void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

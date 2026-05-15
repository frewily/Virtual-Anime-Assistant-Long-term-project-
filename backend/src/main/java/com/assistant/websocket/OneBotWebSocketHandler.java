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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OneBotWebSocketHandler extends TextWebSocketHandler {

    private final AtomicReference<WebSocketSession> botSession = new AtomicReference<>();
    private final MessageRouterService routerService;
    private final ObjectMapper objectMapper;

    public OneBotWebSocketHandler(@Lazy MessageRouterService routerService, ObjectMapper objectMapper) {
        this.routerService = routerService;
        this.objectMapper = objectMapper;
    }

    public WebSocketSession getBotSession() {
        return botSession.get();
    }

    public boolean isConnected() {
        WebSocketSession session = botSession.get();
        return session != null && session.isOpen();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        botSession.set(session);
        System.out.println("OneBot connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        botSession.compareAndSet(session, null);
        System.out.println("OneBot disconnected: " + session.getId() + " status: " + status);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);
        String postType = (String) event.get("post_type");

        if ("message".equals(postType)) {
            handleMessageEvent(event);
        } else if ("notice".equals(postType)) {
            handleNoticeEvent(event);
        } else if ("meta_event".equals(postType)) {
            handleMetaEvent(event);
        }
    }

    private void handleMessageEvent(Map<String, Object> event) {
        String messageType = (String) event.get("message_type");
        String rawMessage = extractMessageText(event);
        if (rawMessage == null || rawMessage.isEmpty())
            return;

        long userId = toLong(event.get("user_id"));
        long groupId = "group".equals(messageType) ? toLong(event.get("group_id")) : 0;

        System.out.println("QQ message - type:" + messageType + " user:" + userId + " text:" + rawMessage);

        ChatMessage chatMsg = new ChatMessage();
        chatMsg.setContent(rawMessage);
        chatMsg.setSource("qq");
        chatMsg.setSenderId(String.valueOf(userId));
        chatMsg.setGroupId("group".equals(messageType) ? String.valueOf(groupId) : null);

        routerService.routeMessage(chatMsg);
    }

    @SuppressWarnings("unchecked")
    private String extractMessageText(Map<String, Object> event) {
        Object message = event.get("message");
        if (message instanceof String) {
            return (String) message;
        }
        if (message instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Object segment : (List<Object>) message) {
                if (segment instanceof Map) {
                    Map<String, Object> seg = (Map<String, Object>) segment;
                    if ("text".equals(seg.get("type"))) {
                        Object data = seg.get("data");
                        if (data instanceof Map) {
                            Object text = ((Map<String, Object>) data).get("text");
                            if (text != null)
                                sb.append(text);
                        }
                    }
                }
            }
            return sb.toString();
        }
        return (String) event.getOrDefault("raw_message", "");
    }

    private void handleNoticeEvent(Map<String, Object> event) {
        String noticeType = (String) event.get("notice_type");
        System.out.println("OneBot notice: " + noticeType);
    }

    private void handleMetaEvent(Map<String, Object> event) {
        String metaType = (String) event.get("meta_event_type");
        if ("heartbeat".equals(metaType)) {
            return;
        }
        System.out.println("OneBot meta event: " + metaType);
    }

    private long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
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
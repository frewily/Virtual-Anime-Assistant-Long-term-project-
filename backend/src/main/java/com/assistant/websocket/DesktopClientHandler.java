package com.assistant.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 桌面客户端 WebSocket 处理器
 * 
 * <p>处理来自 Electron 桌面应用（Live2D）的 WebSocket 连接和消息。</p>
 * 
 * <p>实现阶段：Phase 5 (虚拟形象)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>管理所有连接的桌面客户端会话</li>
 *   <li>接收客户端消息（交互、聊天）</li>
 *   <li>向所有客户端广播消息（语音、动作）</li>
 * </ul>
 * 
 * <p>连接管理：</p>
 * <pre>
 * Electron 应用启动
 *     ↓
 * 连接 ws://localhost:8080/ws/avatar
 *     ↓
 * afterConnectionEstablished() - 添加到 sessions 集合
 *     ↓
 * 保持连接，等待消息
 *     ↓
 * afterConnectionClosed() - 从 sessions 集合移除
 * </pre>
 * 
 * <p>消息格式：</p>
 * <pre>
 * // 服务端 → 客户端（语音播放）
 * {
 *   "type": "speak",
 *   "audioUrl": "/api/tts/audio/abc123.wav",
 *   "text": "主人，CPU使用率过高了！",
 *   "expression": "worried",
 *   "motion": "shake"
 * }
 * 
 * // 服务端 → 客户端（动作触发）
 * {
 *   "type": "action",
 *   "expression": "happy",
 *   "motion": "wave"
 * }
 * 
 * // 客户端 → 服务端（用户交互）
 * {
 *   "type": "interaction",
 *   "action": "click",
 *   "x": 200,
 *   "y": 300
 * }
 * </pre>
 * 
 * <p>线程安全：</p>
 * <p>使用 CopyOnWriteArraySet 保证多线程环境下的会话管理安全。</p>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 5
 * @see WebSocketConfig
 */
@Component
public class DesktopClientHandler extends TextWebSocketHandler {

    /**
     * 所有已连接的 WebSocket 会话
     * 
     * <p>使用 CopyOnWriteArraySet 保证线程安全。</p>
     * <p>支持多个客户端同时连接（如多台显示器）。</p>
     */
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    /**
     * 连接建立时调用
     * 
     * <p>将新会话添加到 sessions 集合，用于后续广播消息。</p>
     * 
     * @param session WebSocket 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Desktop client connected: " + session.getId());
    }

    /**
     * 连接关闭时调用
     * 
     * <p>从 sessions 集合中移除已关闭的会话。</p>
     * 
     * @param session WebSocket 会话
     * @param status 关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Desktop client disconnected: " + session.getId());
    }

    /**
     * 接收到消息时调用
     * 
     * <p>处理来自客户端的消息，根据消息类型进行不同处理。</p>
     * 
     * <p>消息类型：</p>
     * <ul>
     *   <li>interaction - 用户点击 Live2D 模型</li>
     *   <li>chat - 用户通过桌面发送聊天消息</li>
     * </ul>
     * 
     * @param session WebSocket 会话
     * @param message 文本消息（JSON 格式）
     * @throws Exception 处理异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
        // TODO: Phase 5 实现 - 解析消息并调用 MessageRouterService 处理
    }

    /**
     * 向所有连接的客户端广播消息
     * 
     * <p>用于向所有桌面客户端推送语音、动作等消息。</p>
     * 
     * <p>使用场景：</p>
     * <ul>
     *   <li>系统检测到场景触发 → 推送语音和动作</li>
     *   <li>QQ 收到消息 → 推送到桌面显示</li>
     *   <li>定时提醒 → 推送通知</li>
     * </ul>
     * 
     * <p>调用示例：</p>
     * <pre>
     * desktopClientHandler.broadcast("{\"type\":\"speak\",\"text\":\"主人你好\"}");
     * </pre>
     * 
     * @param message JSON 格式的消息字符串
     */
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

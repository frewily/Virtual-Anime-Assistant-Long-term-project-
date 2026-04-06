package com.assistant.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 * 
 * <p>配置 WebSocket 端点，用于与 Electron 桌面应用（Live2D）进行实时双向通信。</p>
 * 
 * <p>实现阶段：Phase 5 (虚拟形象)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>启用 WebSocket 支持</li>
 *   <li>注册 WebSocket 端点 /ws/avatar</li>
 *   <li>配置跨域访问（允许所有来源）</li>
 * </ul>
 * 
 * <p>通信架构：</p>
 * <pre>
 * Electron 桌面应用 (Live2D)
 *         ↓ WebSocket 连接
 * ws://localhost:8080/ws/avatar
 *         ↓
 * DesktopClientHandler (消息处理)
 *         ↓
 * Spring Boot 后端
 * </pre>
 * 
 * <p>消息类型：</p>
 * <ul>
 *   <li>服务端 → 客户端：speak（语音）、action（动作）、status（状态）</li>
 *   <li>客户端 → 服务端：interaction（交互）、chat（聊天）</li>
 * </ul>
 * 
 * <p>使用示例（前端）：</p>
 * <pre>
 * const ws = new WebSocket('ws://localhost:8080/ws/avatar');
 * ws.onmessage = (event) => {
 *   const message = JSON.parse(event.data);
 *   if (message.type === 'speak') {
 *     // 播放语音，触发 Live2D 动作
 *   }
 * };
 * </pre>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 5
 * @see DesktopClientHandler
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /** 桌面客户端消息处理器 */
    private final DesktopClientHandler desktopClientHandler;

    /**
     * 构造函数，通过依赖注入获取消息处理器
     * 
     * @param desktopClientHandler 桌面客户端消息处理器
     */
    public WebSocketConfig(DesktopClientHandler desktopClientHandler) {
        this.desktopClientHandler = desktopClientHandler;
    }

    /**
     * 注册 WebSocket 处理器
     * 
     * <p>将 /ws/avatar 端点映射到 DesktopClientHandler。</p>
     * <p>允许所有来源的跨域访问（生产环境应限制）。</p>
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(desktopClientHandler, "/ws/avatar")
                .setAllowedOrigins("*");
    }
}

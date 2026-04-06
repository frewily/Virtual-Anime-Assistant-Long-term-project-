package com.assistant.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 虚拟形象控制器
 * 
 * <p>管理 Live2D 虚拟形象的状态和动作。</p>
 * 
 * <p>实现阶段：Phase 5 (虚拟形象)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>查询 Live2D 连接状态</li>
 *   <li>触发 Live2D 表情和动作</li>
 *   <li>管理模型切换</li>
 * </ul>
 * 
 * <p>接口列表：</p>
 * <ul>
 *   <li>GET /api/avatar/status - 获取形象状态</li>
 *   <li>POST /api/avatar/action - 执行动作</li>
 *   <li>GET /api/avatar/models - 获取可用模型列表</li>
 * </ul>
 * 
 * <p>与 WebSocket 的关系：</p>
 * <pre>
 * HTTP API (本控制器)          WebSocket
 *     ↓                           ↓
 * 单次动作触发              实时双向通信
 *     ↓                           ↓
 * DesktopClientHandler ← → Electron (Live2D)
 * </pre>
 * 
 * <p>使用场景：</p>
 * <ul>
 *   <li>测试 Live2D 动作</li>
 *   <li>手动触发表情</li>
 *   <li>检查连接状态</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 5
 * @see com.assistant.websocket.DesktopClientHandler
 */
@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    /**
     * 获取形象状态
     * 
     * <p>返回 Live2D 连接状态、当前表情等信息。</p>
     * 
     * <p>TODO: Phase 5 实现</p>
     * 
     * @return 形象状态信息
     */
    @GetMapping("/status")
    public Object getStatus() {
        // TODO: Phase 5 实现
        return null;
    }

    /**
     * 执行动作
     * 
     * <p>让 Live2D 执行指定的表情和动作。</p>
     * 
     * <p>TODO: Phase 5 实现</p>
     * 
     * @param action 动作请求
     */
    @PostMapping("/action")
    public void performAction(@RequestBody Object action) {
        // TODO: Phase 5 实现
    }

    /**
     * 获取可用模型列表
     * 
     * <p>返回所有可用的 Live2D 模型。</p>
     * 
     * <p>TODO: Phase 5 实现</p>
     * 
     * @return 模型列表
     */
    @GetMapping("/models")
    public Object getModels() {
        // TODO: Phase 5 实现
        return null;
    }
}

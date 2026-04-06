package com.assistant.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 消息处理控制器
 * 
 * <p>处理来自 QQ 和桌面的消息，生成智能回复。</p>
 * 
 * <p>实现阶段：Phase 4 (智能响应) + Phase 6 (QQ集成)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>接收用户消息</li>
 *   <li>解析消息意图</li>
 *   <li>生成智能回复</li>
 *   <li>分发到各终端</li>
 * </ul>
 * 
 * <p>消息处理流程：</p>
 * <pre>
 * 用户发送消息
 *     ↓
 * POST /api/message
 *     ↓
 * MessageController
 *     ↓
 * MessageRouterService
 *     ↓
 * ┌─────────────────────────────────────┐
 * │  1. 解析消息意图                    │
 * │  2. 匹配回复模板或调用 LLM          │
 * │  3. 生成 ReplyAction                │
 * │  4. 调用 TTS 生成语音               │
 * │  5. 分发到 QQ 和/或 Live2D          │
 * └─────────────────────────────────────┘
 *     ↓
 * 返回回复结果
 * </pre>
 * 
 * <p>意图识别示例：</p>
 * <table border="1">
 *   <tr><th>用户消息</th><th>识别意图</th><th>回复</th></tr>
 *   <tr><td>"CPU使用率"</td><td>query_status</td><td>"当前 CPU 使用率 35%"</td></tr>
 *   <tr><td>"在干嘛"</td><td>greeting</td><td>"我在看主人工作呢~"</td></tr>
 *   <tr><td>"摸鱼了"</td><td>idle_detect</td><td>"主人又在偷懒！"</td></tr>
 * </table>
 * 
 * <p>接口列表：</p>
 * <ul>
 *   <li>POST /api/message - 发送消息</li>
 *   <li>POST /api/message/trigger - 触发场景</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 发送消息
 * curl -X POST http://localhost:8080/api/message \
 *   -H "Content-Type: application/json" \
 *   -d '{
 *     "source": "qq",
 *     "senderId": "12345678",
 *     "content": "今天CPU使用率怎么样？"
 *   }'
 * 
 * // 触发场景
 * curl -X POST http://localhost:8080/api/message/trigger \
 *   -H "Content-Type: application/json" \
 *   -d '{
 *     "scenarioId": "high_cpu",
 *     "params": {"value": "85%"}
 *   }'
 * </pre>
 * 
 * <p>后续扩展：</p>
 * <ul>
 *   <li>集成大语言模型（ChatGPT/本地 LLM）</li>
 *   <li>支持上下文对话</li>
 *   <li>支持多轮对话</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 4
 * @see com.assistant.service.MessageRouterService
 * @see com.assistant.model.ChatMessage
 * @see com.assistant.model.ReplyAction
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    /**
     * 发送消息
     * 
     * <p>接收用户消息，生成回复并分发。</p>
     * 
     * <p>TODO: Phase 4 实现</p>
     * 
     * @param message 聊天消息
     * @return 回复结果
     */
    @PostMapping
    public Object sendMessage(@RequestBody Object message) {
        // TODO: Phase 4 实现
        return null;
    }

    /**
     * 触发场景
     * 
     * <p>手动触发特定场景，生成对应的回复。</p>
     * 
     * <p>TODO: Phase 4 实现</p>
     * 
     * @param trigger 场景触发请求
     * @return 场景响应
     */
    @PostMapping("/trigger")
    public Object triggerScenario(@RequestBody Object trigger) {
        // TODO: Phase 4 实现
        return null;
    }
}

package com.assistant.model;

/**
 * 聊天消息模型
 * 
 * <p>
 * 表示来自 QQ 或桌面的聊天消息。
 * </p>
 * 
 * <p>
 * 实现阶段：Phase 4 (智能响应) + Phase 6 (QQ集成)
 * </p>
 * 
 * <p>
 * 消息来源：
 * </p>
 * <ul>
 * <li>QQ 消息：通过 OneBot 协议上报</li>
 * <li>桌面交互：通过 WebSocket 上报</li>
 * </ul>
 * 
 * <p>
 * JSON 示例：
 * </p>
 * 
 * <pre>
 * // QQ 消息
 * {
 *   "source": "qq",
 *   "senderId": "12345678",
 *   "content": "今天CPU使用率怎么样？"
 * }
 * 
 * // 桌面交互
 * {
 *   "source": "desktop",
 *   "senderId": "local_user",
 *   "content": "你好呀"
 * }
 * </pre>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 4
 * @see com.assistant.service.MessageRouterService
 */
public class ChatMessage {

    private String source;
    private String content;
    private String senderId;
    private String groupId;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

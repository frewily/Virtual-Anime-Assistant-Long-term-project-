package com.assistant.model;

/**
 * 聊天消息模型
 * 
 * <p>表示来自 QQ 或桌面的聊天消息。</p>
 * 
 * <p>实现阶段：Phase 4 (智能响应) + Phase 6 (QQ集成)</p>
 * 
 * <p>消息来源：</p>
 * <ul>
 *   <li>QQ 消息：通过 OneBot 协议上报</li>
 *   <li>桌面交互：通过 WebSocket 上报</li>
 * </ul>
 * 
 * <p>JSON 示例：</p>
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
    
    /** 
     * 消息来源
     * <p>可选值：qq、desktop、scenario</p>
     */
    private String source;
    
    /** 
     * 消息内容
     */
    private String content;
    
    /** 
     * 发送者 ID
     * <p>QQ 用户 ID 或桌面用户标识</p>
     */
    private String senderId;

    /**
     * 获取消息来源
     * 
     * @return 消息来源
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置消息来源
     * 
     * @param source 消息来源
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取消息内容
     * 
     * @return 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容
     * 
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取发送者 ID
     * 
     * @return 发送者 ID
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * 设置发送者 ID
     * 
     * @param senderId 发送者 ID
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

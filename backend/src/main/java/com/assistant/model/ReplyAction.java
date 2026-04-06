package com.assistant.model;

/**
 * 回复动作模型
 * 
 * <p>表示助手对用户消息的完整回复，包含文字、表情、动作和分发目标。</p>
 * 
 * <p>实现阶段：Phase 4 (智能响应)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>定义回复文字内容</li>
 *   <li>定义 Live2D 表情和动作</li>
 *   <li>定义分发目标（QQ、桌面）</li>
 * </ul>
 * 
 * <p>生成流程：</p>
 * <pre>
 * 用户消息 → MessageRouterService
 *     ↓
 * 解析意图，匹配场景
 *     ↓
 * 生成 ReplyAction
 *     ↓
 * 分发到 QQ 和/或 Live2D
 * </pre>
 * 
 * <p>JSON 示例：</p>
 * <pre>
 * {
 *   "text": "主人，CPU使用率已经超过80%了哦！",
 *   "expression": "worried",
 *   "motion": "shake",
 *   "sendToQQ": true,
 *   "sendToDesktop": true
 * }
 * </pre>
 * 
 * <p>场景示例：</p>
 * <table border="1">
 *   <tr><th>场景</th><th>text</th><th>expression</th><th>motion</th></tr>
 *   <tr><td>高CPU</td><td>"电脑好热啊..."</td><td>worried</td><td>shake</td></tr>
 *   <tr><td>摸鱼</td><td>"又在玩游戏！"</td><td>angry</td><td>point</td></tr>
 *   <tr><td>深夜</td><td>"早点休息吧~"</td><td>sleepy</td><td>yawn</td></tr>
 *   <tr><td>专注</td><td>"喝杯水吧！"</td><td>happy</td><td>wave</td></tr>
 * </table>
 * 
 * <p>后续扩展：</p>
 * <ul>
 *   <li>添加音频 URL 字段</li>
 *   <li>添加图片 URL 字段</li>
 *   <li>支持多段动作序列</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 4
 * @see com.assistant.service.MessageRouterService
 * @see com.assistant.service.ScenarioEngine
 */
public class ReplyAction {
    
    /** 
     * 回复文字内容
     * <p>发送到 QQ 的文字，也是 TTS 合成的文本</p>
     */
    private String text;
    
    /** 
     * Live2D 表情名称
     * <p>可选值：idle、happy、worried、angry、sleepy、curious</p>
     */
    private String expression;
    
    /** 
     * Live2D 动作名称
     * <p>可选值：idle、wave、shake、point、yawn、tap_body</p>
     */
    private String motion;
    
    /** 
     * 是否发送到 QQ
     * <p>为 true 时，文字会发送到 QQ</p>
     */
    private boolean sendToQQ;
    
    /** 
     * 是否发送到桌面
     * <p>为 true 时，会触发 Live2D 动作和语音播放</p>
     */
    private boolean sendToDesktop;

    /**
     * 获取回复文字
     * 
     * @return 回复文字
     */
    public String getText() {
        return text;
    }

    /**
     * 设置回复文字
     * 
     * @param text 回复文字
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取表情名称
     * 
     * @return 表情名称
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 设置表情名称
     * 
     * @param expression 表情名称
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * 获取动作名称
     * 
     * @return 动作名称
     */
    public String getMotion() {
        return motion;
    }

    /**
     * 设置动作名称
     * 
     * @param motion 动作名称
     */
    public void setMotion(String motion) {
        this.motion = motion;
    }

    /**
     * 是否发送到 QQ
     * 
     * @return true 表示发送到 QQ
     */
    public boolean isSendToQQ() {
        return sendToQQ;
    }

    /**
     * 设置是否发送到 QQ
     * 
     * @param sendToQQ 是否发送到 QQ
     */
    public void setSendToQQ(boolean sendToQQ) {
        this.sendToQQ = sendToQQ;
    }

    /**
     * 是否发送到桌面
     * 
     * @return true 表示发送到桌面
     */
    public boolean isSendToDesktop() {
        return sendToDesktop;
    }

    /**
     * 设置是否发送到桌面
     * 
     * @param sendToDesktop 是否发送到桌面
     */
    public void setSendToDesktop(boolean sendToDesktop) {
        this.sendToDesktop = sendToDesktop;
    }
}

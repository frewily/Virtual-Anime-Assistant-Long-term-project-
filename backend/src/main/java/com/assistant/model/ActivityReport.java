package com.assistant.model;

import java.time.LocalDateTime;

/**
 * 活动窗口报告模型
 * 
 * <p>表示用户当前活动窗口的信息，由 Python Agent 上报。</p>
 * 
 * <p>实现阶段：Phase 2 (窗口监控)</p>
 * 
 * <p>数据来源：</p>
 * <ul>
 *   <li>Python Agent 通过 Windows API 获取前台窗口标题</li>
 *   <li>Agent 通过模式匹配识别应用名称</li>
 *   <li>Agent 通过 HTTP POST 上报到后端</li>
 * </ul>
 * 
 * <p>JSON 示例：</p>
 * <pre>
 * {
 *   "appId": "vs_code",
 *   "windowTitle": "Main.java — Visual Studio Code",
 *   "timestamp": "2024-01-15T14:30:00"
 * }
 * </pre>
 * 
 * <p>应用识别示例：</p>
 * <pre>
 * 窗口标题: "Main.java — Visual Studio Code"
 *     ↓
 * 模式匹配: 包含 "Visual Studio Code"
 *     ↓
 * appId: "vs_code"
 * appName: "VS Code"
 * </pre>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 2
 * @see com.assistant.service.ActivityService
 * @see com.assistant.controller.ReportController
 */
public class ActivityReport {
    
    /** 
     * 应用标识
     * <p>小写下划线格式，如 "vs_code"、"idea"、"chrome"</p>
     */
    private String appId;
    
    /** 
     * 窗口完整标题
     * <p>如 "Main.java — Visual Studio Code"</p>
     */
    private String windowTitle;
    
    /** 
     * 上报时间戳
     * <p>由后端自动设置，表示接收上报的时间</p>
     */
    private LocalDateTime timestamp;

    /**
     * 获取应用标识
     * 
     * @return 应用标识，如 "vs_code"
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置应用标识
     * 
     * @param appId 应用标识
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取窗口标题
     * 
     * @return 窗口完整标题
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * 设置窗口标题
     * 
     * @param windowTitle 窗口标题
     */
    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * 获取上报时间戳
     * 
     * @return 上报时间
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 设置上报时间戳
     * 
     * @param timestamp 上报时间
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

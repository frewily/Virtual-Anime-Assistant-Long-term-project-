package com.assistant.service;

import com.assistant.model.ActivityReport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 活动窗口服务
 * 
 * <p>管理用户当前活动的应用程序窗口信息。</p>
 * 
 * <p>实现阶段：Phase 2 (窗口监控)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>接收 Python Agent 上报的活动窗口</li>
 *   <li>维护当前活动窗口状态</li>
 *   <li>提供当前活动窗口查询接口</li>
 * </ul>
 * 
 * <p>数据流程：</p>
 * <pre>
 * Python Agent 检测窗口变化
 *     ↓
 * POST /api/report/window
 *     ↓
 * ActivityService.updateActivity()
 *     ↓
 * 存储当前活动状态
 *     ↓
 * GET /api/activity/current 查询
 * </pre>
 * 
 * <p>后续扩展：</p>
 * <ul>
 *   <li>Phase 4: 添加活动历史记录</li>
 *   <li>Phase 4: 添加应用使用时长统计</li>
 *   <li>Phase 4: 添加场景检测（如摸鱼检测）</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 2
 * @see ActivityReport
 * @see com.assistant.controller.ReportController
 */
@Service
public class ActivityService {

    /** 当前活动窗口报告，保存最近一次上报的活动信息 */
    private ActivityReport currentActivity;

    /**
     * 更新活动窗口
     * 
     * <p>由 ReportController 调用，接收 Agent 上报的窗口信息并更新当前活动状态。</p>
     * <p>自动设置上报时间戳。</p>
     * 
     * @param report 活动报告，包含 appId、windowTitle 等信息
     */
    public void updateActivity(ActivityReport report) {
        report.setTimestamp(LocalDateTime.now());
        this.currentActivity = report;
    }

    /**
     * 获取当前活动窗口
     * 
     * <p>返回最近一次上报的活动窗口信息。</p>
     * 
     * @return ActivityReport 当前活动窗口报告，如果没有上报过则返回 null
     */
    public ActivityReport getCurrentActivity() {
        return currentActivity;
    }
}

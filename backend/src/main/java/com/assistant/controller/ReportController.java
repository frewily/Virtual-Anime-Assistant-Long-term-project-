package com.assistant.controller;

import com.assistant.model.ActivityReport;
import com.assistant.service.ActivityService;
import org.springframework.web.bind.annotation.*;

/**
 * 活动窗口上报控制器
 * 
 * <p>接收 Python Agent 上报的活动窗口信息，用于追踪用户当前正在使用的应用程序。</p>
 * 
 * <p>实现阶段：Phase 2 (窗口监控)</p>
 * 
 * <p>接口列表：</p>
 * <ul>
 *   <li>POST /api/report/window - 接收 Agent 上报的窗口信息</li>
 *   <li>GET /api/activity/current - 查询当前活动窗口</li>
 * </ul>
 * 
 * <p>工作流程：</p>
 * <pre>
 * Python Agent 检测到窗口变化
 *     ↓
 * POST /api/report/window
 *     ↓
 * ActivityService 更新当前活动
 *     ↓
 * 可通过 GET /api/activity/current 查询
 * </pre>
 * 
 * <p>使用示例：</p>
 * <pre>
 * # Agent 上报窗口
 * curl -X POST http://localhost:8080/api/report/window \
 *   -H "Content-Type: application/json" \
 *   -d '{"appId":"vs_code","windowTitle":"Main.java - VS Code"}'
 * 
 * # 查询当前活动
 * curl http://localhost:8080/api/activity/current
 * </pre>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 2
 * @see ActivityService
 * @see ActivityReport
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ActivityService activityService;

    /**
     * 构造函数，通过依赖注入获取活动服务
     * 
     * @param activityService 活动服务，负责管理当前活动窗口状态
     */
    public ReportController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 上报活动窗口
     * 
     * <p>由 Python Agent 调用，上报当前活动窗口信息。</p>
     * <p>Agent 会定期（如每 5 秒）检测前台窗口，当窗口变化时调用此接口。</p>
     * 
     * @param report 活动报告，包含 appId、windowTitle 等信息
     */
    @PostMapping("/window")
    public void reportWindow(@RequestBody ActivityReport report) {
        activityService.updateActivity(report);
    }

    /**
     * 获取当前活动窗口
     * 
     * <p>返回最近一次上报的活动窗口信息。</p>
     * 
     * @return ActivityReport 当前活动窗口报告，如果没有上报过则返回 null
     */
    @GetMapping("/activity/current")
    public ActivityReport getCurrentActivity() {
        return activityService.getCurrentActivity();
    }
}

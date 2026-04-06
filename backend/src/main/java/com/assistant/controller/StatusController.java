package com.assistant.controller;

import com.assistant.model.SystemStatus;
import com.assistant.service.SystemMonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统状态控制器
 * @author Assistant
 * @version 1.0
 * @since Phase 1
 */
@RestController
@RequestMapping("/api")
public class StatusController {

    //private final:使代码块更安全，不允许被修改
    private final SystemMonitorService monitorService;

    /**
     * 构造函数，通过依赖注入获取系统监控服务
     * 
     * @param monitorService 系统监控服务，负责获取硬件数据
     */
    public StatusController(SystemMonitorService monitorService) {
        this.monitorService = monitorService;
    }

    //    构造器注入 vs @Autowired 字段注入
    //
    //    使用原则：
    //    默认用构造器注入
    //    只有可选依赖才考虑 @Autowired
    //    Spring 官方推荐构造器注入
    //    简单说：能用构造器就用构造器，更安全、更好测试、更清晰。

    /**
     * 获取系统状态
     * 
     * <p>返回当前系统的 CPU 使用率、内存使用情况和系统运行时间。</p>
     * 
     * <p>实现阶段：Phase 1 (MVP)</p>
     * 
     * @return SystemStatus 包含 CPU、内存、运行时间的状态对象
     */
    @GetMapping("/status")
    public SystemStatus getStatus() {
        return monitorService.getSystemStatus();
    }
}

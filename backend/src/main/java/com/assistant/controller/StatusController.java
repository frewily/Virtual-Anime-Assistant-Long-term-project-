package com.assistant.controller;

import com.assistant.model.SystemStatus;
import com.assistant.service.SystemMonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final SystemMonitorService monitorService;

    public StatusController(SystemMonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/status")
    public SystemStatus getStatus() {
        return monitorService.getSystemStatus();
    }
}

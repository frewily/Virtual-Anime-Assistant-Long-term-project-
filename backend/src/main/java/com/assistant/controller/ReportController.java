package com.assistant.controller;

import com.assistant.model.ActivityReport;
import com.assistant.service.ActivityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ActivityService activityService;

    public ReportController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/window")
    public void reportWindow(@RequestBody ActivityReport report) {
        activityService.updateActivity(report);
    }

    @GetMapping("/activity/current")
    public ActivityReport getCurrentActivity() {
        return activityService.getCurrentActivity();
    }
}

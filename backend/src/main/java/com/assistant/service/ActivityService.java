package com.assistant.service;

import com.assistant.model.ActivityReport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityService {

    private ActivityReport currentActivity;

    public void updateActivity(ActivityReport report) {
        report.setTimestamp(LocalDateTime.now());
        this.currentActivity = report;
    }

    public ActivityReport getCurrentActivity() {
        return currentActivity;
    }
}

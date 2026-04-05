package com.assistant.model;

public record SystemStatus(
    String cpuUsage,
    String memoryUsed,
    String memoryTotal,
    String memoryUsage,
    String uptime
) {}

package com.assistant.service;

import com.assistant.model.SystemStatus;

/**
 * 系统监控服务接口
 * 
 * <p>提供获取系统硬件状态的功能，包括 CPU 使用率、内存使用情况和系统运行时间。</p>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 1
 * @see SystemStatus
 */
public interface SystemMonitorService {

    /**
     * 获取系统状态
     * 
     * @return SystemStatus 包含 CPU、内存、运行时间的状态对象
     */
    SystemStatus getSystemStatus();
}

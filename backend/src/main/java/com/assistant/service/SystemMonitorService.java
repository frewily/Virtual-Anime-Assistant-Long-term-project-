package com.assistant.service;

import com.assistant.model.SystemStatus;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.concurrent.TimeUnit;

@Service
public class SystemMonitorService {

    private final SystemInfo systemInfo;
    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private final OperatingSystem os;
    
    private long[] prevTicks;

    public SystemMonitorService() {
        this.systemInfo = new SystemInfo();
        this.processor = systemInfo.getHardware().getProcessor();
        this.memory = systemInfo.getHardware().getMemory();
        this.os = systemInfo.getOperatingSystem();
        this.prevTicks = processor.getSystemCpuLoadTicks();
    }

    public SystemStatus getSystemStatus() {
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();
        
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double memoryUsage = (double) usedMemory / totalMemory * 100;
        
        long uptimeSeconds = os.getSystemUptime();
        String uptime = formatUptime(uptimeSeconds);
        
        return new SystemStatus(
            String.format("%.1f%%", cpuLoad),
            formatMemory(usedMemory),
            formatMemory(totalMemory),
            String.format("%.1f%%", memoryUsage),
            uptime
        );
    }
    
    private String formatMemory(long bytes) {
        long gb = bytes / (1024 * 1024 * 1024);
        return gb + "GB";
    }
    
    private String formatUptime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + "小时" + minutes + "分钟";
    }
}

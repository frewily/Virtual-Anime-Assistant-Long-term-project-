package com.assistant.service.Impl;

import com.assistant.model.SystemStatus;
import com.assistant.service.SystemMonitorService;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;

/**
 * 系统监控服务实现类
 *
 * <p>使用 OSHI 库获取系统硬件状态，包括 CPU 使用率、内存使用情况和系统运行时间。</p>
 *
 * @author Assistant
 * @version 1.0
 * @since Phase 1
 * @see SystemMonitorService
 * @see com.assistant.model.SystemStatus
 */
@Service
public class SystemMonitorServiceImpl implements SystemMonitorService {

    /**
     * OSHI 系统信息对象，用于获取硬件和操作系统信息
     */
    private final SystemInfo systemInfo;

    /**
     * CPU 处理器对象，用于获取 CPU 负载信息
     */
    private final CentralProcessor processor;

    /**
     * 内存对象，用于获取内存使用信息
     */
    private final GlobalMemory memory;

    /**
     * 操作系统对象，用于获取系统运行时间等
     */
    private final OperatingSystem os;

    /**
     * 上一次的 CPU tick 值，用于计算两次采样之间的 CPU 负载
     */
    private long[] prevTicks;

    /**
     * 构造函数，初始化 OSHI 组件
     *
     * <p>创建 SystemInfo 实例并获取硬件组件引用。</p>
     * <p>同时初始化 CPU tick 基准值，用于后续计算 CPU 使用率。</p>
     */
    public SystemMonitorServiceImpl() {
        this.systemInfo = new SystemInfo();
        this.processor = systemInfo.getHardware().getProcessor();
        this.memory = systemInfo.getHardware().getMemory();
        this.os = systemInfo.getOperatingSystem();
        this.prevTicks = processor.getSystemCpuLoadTicks();
    }

    /**
     * 获取系统状态
     *
     * <p>返回包含 CPU、内存、运行时间的系统状态对象。</p>
     *
     * <p>计算流程：</p>
     * <ol>
     *   <li>计算 CPU 使用率（基于两次采样的 tick 差值）</li>
     *   <li>计算内存使用情况</li>
     *   <li>获取系统运行时间并格式化</li>
     *   <li>返回封装好的 SystemStatus 对象</li>
     * </ol>
     *
     * @return SystemStatus 系统状态对象，包含格式化后的状态信息
     */
    @Override
    public SystemStatus getSystemStatus() {
        // 计算 CPU 使用率
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();

        // 计算内存使用情况
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double memoryUsage = (double) usedMemory / totalMemory * 100;

        // 获取系统运行时间
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

    /**
     * 格式化内存大小
     *
     * <p>将字节数转换为 GB 单位的可读字符串。</p>
     *
     * @param bytes 内存字节数
     * @return 格式化后的字符串，如 "8GB"、"16GB"
     */
    private String formatMemory(long bytes) {
        long gb = bytes / (1024 * 1024 * 1024);
        return gb + "GB";
    }

    /**
     * 格式化运行时间
     *
     * <p>将秒数转换为 "X小时X分钟" 格式的可读字符串。</p>
     *
     * @param seconds 运行秒数
     * @return 格式化后的字符串，如 "2小时15分钟"
     */
    private String formatUptime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + "小时" + minutes + "分钟";
    }
}

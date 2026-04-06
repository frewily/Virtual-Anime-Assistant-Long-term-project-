package com.assistant.model;

/**
 * 系统状态模型
 * 
 * <p>表示系统硬件状态的不可变数据对象，包含 CPU、内存和运行时间信息。</p>
 * 
 * <p>实现阶段：Phase 1 (MVP)</p>
 * 
 * <p>使用 Java 14+ 的 record 类型，自动生成：</p>
 * <ul>
 *   <li>构造函数</li>
 *   <li>getter 方法</li>
 *   <li>equals、hashCode、toString 方法</li>
 * </ul>
 * 
 * <p>JSON 序列化示例：</p>
 * <pre>
 * {
 *   "cpuUsage": "25.5%",
 *   "memoryUsed": "8.2GB",
 *   "memoryTotal": "16GB",
 *   "memoryUsage": "51.2%",
 *   "uptime": "2小时15分钟"
 * }
 * </pre>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 1
 * @see com.assistant.service.SystemMonitorService
 * @see com.assistant.controller.StatusController
 */
public record SystemStatus(
    /** CPU 使用率百分比，如 "25.5%" */
    String cpuUsage,
    
    /** 已使用内存，如 "8.2GB" */
    String memoryUsed,
    
    /** 总内存，如 "16GB" */
    String memoryTotal,
    
    /** 内存使用率百分比，如 "51.2%" */
    String memoryUsage,
    
    /** 系统运行时间，如 "2小时15分钟" */
    String uptime
) {}

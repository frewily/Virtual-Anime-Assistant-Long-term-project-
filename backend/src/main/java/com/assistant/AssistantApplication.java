package com.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 二次元桌面助手 - 后端服务启动类
 * 
 * <p>这是整个后端服务的入口点，负责启动 Spring Boot 应用。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>自动配置 Spring Boot 应用</li>
 *   <li>扫描并注册所有组件（Controller、Service、WebSocket 等）</li>
 *   <li>启动内嵌 Tomcat 服务器（默认端口 8080）</li>
 * </ul>
 * 
 * <p>启动后可访问：</p>
 * <ul>
 *   <li>API 接口: http://localhost:8080/api/*</li>
 *   <li>WebSocket: ws://localhost:8080/ws/avatar</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 1
 */
@SpringBootApplication
public class AssistantApplication {

    /**
     * 应用程序入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
    }
}

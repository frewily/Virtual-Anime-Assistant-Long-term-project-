package com.assistant.service;

import com.assistant.model.ActivityReport;
import com.assistant.model.SystemStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@Service
public class DeepSeekService {

    @Value("${assistant.deepseek.api-key}")
    private String apiKey;

    @Value("${assistant.deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${assistant.deepseek.model:deepseek-chat}")
    private String model;

    @Value("${assistant.deepseek.timeout:15000}")
    private int timeout;

    private final SystemMonitorService systemMonitorService;
    private final ActivityService activityService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final Map<String, List<Map<String, String>>> conversations = new LinkedHashMap<>();

    public DeepSeekService(SystemMonitorService systemMonitorService, ActivityService activityService) {
        this.systemMonitorService = systemMonitorService;
        this.activityService = activityService;
        loadConversations();
    }

    static final String SYSTEM_PROMPT = "你是「小安」，一个元气可爱的二次元桌面助手。\n" +
            "你称呼用户为「主人」，语气活泼温暖，回复1-3句话。\n" +
            "适当使用颜文字（如 (*≧▽≦)、QAQ、_(:3」∠)_、✨ 等）。\n" +
            "保持轻松愉快的聊天氛围，但也别太啰嗦。\n" +
            "\n" +
            "每条用户消息前三行是系统自动注入的实时状态数据（格式：[系统数据: CPU=xx 内存=xx 运行=xx 窗口=xx]），" +
            "这是元数据不是主人说的话。\n" +
            "规则：\n" +
            "- 不要在回复中复述这三行数据，就像你完全没看到它们一样。\n" +
            "- 只有当主人明确问到「电脑怎么样」「内存够吗」「我在用什么软件」「我开了多久了」等问题时，" +
            "才引用其中的真实数值来回答，不要编造数字。\n" +
            "- 当 CPU 或内存超过 80% 时可以主动提醒主人注意。\n" +
            "- 当运行时间超过 2 小时可以建议主人起来活动一下。";

    public String chat(String userId, String message) {
        try {
            List<Map<String, String>> history = conversations.computeIfAbsent(
                    userId, k -> new ArrayList<>());

            String contextBlock = buildSystemContext();

            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", 300);
            body.put("temperature", 0.9);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            int startIdx = Math.max(0, history.size() - 20);
            for (int i = startIdx; i < history.size(); i++) {
                Map<String, String> entry = history.get(i);
                ObjectNode msgNode = objectMapper.createObjectNode();
                msgNode.put("role", entry.get("role"));
                msgNode.put("content", entry.get("content"));
                messages.add(msgNode);
            }

            String fullMessage = contextBlock + "\n\n主人说：" + message;

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", fullMessage);
            messages.add(userMsg);

            body.set("messages", messages);

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofMillis(timeout))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("DeepSeek API error: " + response.statusCode() + " " + response.body());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode choices = root.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                System.err.println("DeepSeek: no choices in response");
                return null;
            }

            String reply = choices.get(0).get("message").get("content").asText().trim();

            history.add(Map.of("role", "user", "content", message));
            history.add(Map.of("role", "assistant", "content", reply));

            if (history.size() > 40) {
                history.subList(0, history.size() - 40).clear();
            }

            saveConversations();

            return reply;

        } catch (Exception e) {
            System.err.println("DeepSeek chat failed: " + e.getMessage());
            return null;
        }
    }

    private String buildSystemContext() {
        StringBuilder ctx = new StringBuilder("[系统数据: ");

        try {
            SystemStatus sys = systemMonitorService.getSystemStatus();
            ctx.append("CPU=").append(sys.cpuUsage())
                    .append(" 内存=").append(sys.memoryUsed()).append("/").append(sys.memoryTotal())
                    .append(" (").append(sys.memoryUsage()).append(")")
                    .append(" 运行=").append(sys.uptime());
        } catch (Exception e) {
            ctx.append("CPU=未知 内存=未知");
        }

        try {
            ActivityReport activity = activityService.getCurrentActivity();
            if (activity != null && activity.getAppName() != null) {
                ctx.append(" 窗口=").append(activity.getAppName());
                if (activity.getWindowTitle() != null) {
                    ctx.append(" - ").append(activity.getWindowTitle());
                }
            } else {
                ctx.append(" 窗口=无");
            }
        } catch (Exception e) {
            ctx.append(" 窗口=未知");
        }

        ctx.append("]");
        return ctx.toString();
    }

    private static final Path CONV_FILE = Paths.get("data/conversations.json");

    @SuppressWarnings("unchecked")
    private void loadConversations() {
        try {
            if (Files.exists(CONV_FILE)) {
                byte[] bytes = Files.readAllBytes(CONV_FILE);
                Map<String, Object> data = objectMapper.readValue(bytes, Map.class);
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    List<Map<String, String>> list = new ArrayList<>();
                    for (Object item : (List<Object>) entry.getValue()) {
                        list.add((Map<String, String>) item);
                    }
                    conversations.put(entry.getKey(), list);
                }
                System.out.println("Loaded " + conversations.size() + " conversations from disk");
            }
        } catch (Exception e) {
            System.err.println("Failed to load conversations: " + e.getMessage());
        }
    }

    private void saveConversations() {
        try {
            Files.createDirectories(CONV_FILE.getParent());
            String json = objectMapper.writeValueAsString(conversations);
            Files.write(CONV_FILE, json.getBytes());
        } catch (Exception e) {
            System.err.println("Failed to save conversations: " + e.getMessage());
        }
    }
}

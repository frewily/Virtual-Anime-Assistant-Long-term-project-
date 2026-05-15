package com.assistant.service;

import com.assistant.model.ActivityReport;
import com.assistant.model.ReplyAction;
import com.assistant.model.SystemStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScenarioEngine {

    private static final long COOLDOWN_MS = 5 * 60 * 1000;

    private final SystemMonitorService systemMonitor;
    private final ActivityService activityService;

    @Value("${assistant.scenarios.config-path:../config/scenarios.yml}")
    private String configPath;

    private List<ScenarioDef> scenarios = new ArrayList<>();
    private final Map<String, Long> lastTriggerTime = new HashMap<>();
    private final Map<String, Long> appStartTimes = new HashMap<>();
    private final Random random = new Random();

    public ScenarioEngine(SystemMonitorService systemMonitor, ActivityService activityService) {
        this.systemMonitor = systemMonitor;
        this.activityService = activityService;
    }

    @PostConstruct
    public void loadScenarios() {
        try (InputStream input = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("scenarios");
            if (list != null) {
                for (Map<String, Object> item : list) {
                    ScenarioDef def = new ScenarioDef();
                    def.id = (String) item.get("id");
                    def.name = (String) item.get("name");

                    @SuppressWarnings("unchecked")
                    Map<String, Object> trigger = (Map<String, Object>) item.get("trigger");
                    if (trigger != null) {
                        def.triggerType = (String) trigger.get("type");
                        def.threshold = trigger.get("threshold") instanceof Integer
                                ? ((Integer) trigger.get("threshold")).doubleValue()
                                : null;
                        def.triggerApps = castToStringList(trigger.get("apps"));
                        def.durationSeconds = trigger.get("duration") instanceof Integer
                                ? ((Integer) trigger.get("duration")).longValue()
                                : null;

                        @SuppressWarnings("unchecked")
                        Map<String, Object> timeRange = (Map<String, Object>) trigger.get("timeRange");
                        if (timeRange != null) {
                            def.timeStart = (String) timeRange.get("start");
                            def.timeEnd = (String) timeRange.get("end");
                        }
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) item.get("response");
                    if (response != null) {
                        def.expression = (String) response.get("expression");
                        def.motion = (String) response.get("motion");
                        def.templates = castToStringList(response.get("templates"));
                    }

                    scenarios.add(def);
                }
            }
            System.out.println("Loaded " + scenarios.size() + " scenarios from " + configPath);
        } catch (Exception e) {
            System.err.println("Failed to load scenarios config: " + e.getMessage());
        }
    }

    public List<ReplyAction> detect() {
        List<ReplyAction> actions = new ArrayList<>();

        for (ScenarioDef scenario : scenarios) {
            if (isInCooldown(scenario.id))
                continue;

            boolean matched = switch (scenario.triggerType) {
                case "cpu_threshold" -> checkCpuThreshold(scenario);
                case "app_detect" -> checkAppDetect(scenario);
                case "time_range" -> checkTimeRange(scenario);
                case "app_duration" -> checkAppDuration(scenario);
                default -> false;
            };

            if (matched) {
                ReplyAction action = buildAction(scenario);
                action.setSendToDesktop(true);
                actions.add(action);
                lastTriggerTime.put(scenario.id, System.currentTimeMillis());
                System.out.println("Scenario triggered: " + scenario.name);
            }
        }

        return actions;
    }

    public ReplyAction trigger(String scenarioId) {
        ScenarioDef scenario = scenarios.stream()
                .filter(s -> s.id.equals(scenarioId))
                .findFirst()
                .orElse(null);
        if (scenario == null)
            return null;

        ReplyAction action = buildAction(scenario);
        action.setSendToDesktop(true);
        return action;
    }

    private ReplyAction buildAction(ScenarioDef scenario) {
        ReplyAction action = new ReplyAction();
        action.setExpression(scenario.expression);
        action.setMotion(scenario.motion);
        action.setSendToDesktop(true);

        if (scenario.templates != null && !scenario.templates.isEmpty()) {
            String text = scenario.templates.get(random.nextInt(scenario.templates.size()));
            ActivityReport activity = activityService.getCurrentActivity();
            if (activity != null && activity.getAppId() != null) {
                text = text.replace("{appName}", activity.getAppId());
            }
            action.setText(text);
        }

        return action;
    }

    private boolean checkCpuThreshold(ScenarioDef scenario) {
        try {
            SystemStatus status = systemMonitor.getSystemStatus();
            String cpuStr = status.cpuUsage().replace("%", "");
            double cpu = Double.parseDouble(cpuStr);
            return cpu >= (scenario.threshold != null ? scenario.threshold : 80);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkAppDetect(ScenarioDef scenario) {
        if (scenario.triggerApps == null || scenario.triggerApps.isEmpty())
            return false;

        ActivityReport activity = activityService.getCurrentActivity();
        if (activity == null || activity.getAppId() == null)
            return false;

        String currentApp = activity.getAppId().toLowerCase();
        boolean appMatches = scenario.triggerApps.stream()
                .map(String::toLowerCase)
                .anyMatch(a -> currentApp.contains(a) || currentApp.equals(a));

        if (!appMatches)
            return false;

        if (scenario.timeStart != null && scenario.timeEnd != null) {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.parse(scenario.timeStart, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(scenario.timeEnd, DateTimeFormatter.ofPattern("HH:mm"));
            if (now.isBefore(start) || now.isAfter(end))
                return false;
        }

        return true;
    }

    private boolean checkTimeRange(ScenarioDef scenario) {
        if (scenario.timeStart == null || scenario.timeEnd == null)
            return false;

        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(scenario.timeStart, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(scenario.timeEnd, DateTimeFormatter.ofPattern("HH:mm"));

        if (start.isBefore(end)) {
            return !now.isBefore(start) && !now.isAfter(end);
        } else {
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }

    private boolean checkAppDuration(ScenarioDef scenario) {
        if (scenario.triggerApps == null || scenario.triggerApps.isEmpty())
            return false;
        if (scenario.durationSeconds == null)
            return false;

        ActivityReport activity = activityService.getCurrentActivity();
        if (activity == null || activity.getAppId() == null)
            return false;

        String currentApp = activity.getAppId().toLowerCase();
        boolean appMatches = scenario.triggerApps.stream()
                .map(String::toLowerCase)
                .anyMatch(a -> currentApp.contains(a) || currentApp.equals(a));

        if (!appMatches) {
            appStartTimes.remove(currentApp);
            return false;
        }

        long now = System.currentTimeMillis();
        appStartTimes.putIfAbsent(currentApp, now);
        long elapsed = (now - appStartTimes.get(currentApp)) / 1000;

        return elapsed >= scenario.durationSeconds;
    }

    private boolean isInCooldown(String scenarioId) {
        Long lastTime = lastTriggerTime.get(scenarioId);
        if (lastTime == null)
            return false;
        return (System.currentTimeMillis() - lastTime) < COOLDOWN_MS;
    }

    @SuppressWarnings("unchecked")
    private List<String> castToStringList(Object obj) {
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return null;
    }

    private static class ScenarioDef {
        String id;
        String name;
        String triggerType;
        Double threshold;
        List<String> triggerApps;
        Long durationSeconds;
        String timeStart;
        String timeEnd;
        String expression;
        String motion;
        List<String> templates;
    }
}
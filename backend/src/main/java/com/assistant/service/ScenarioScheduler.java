package com.assistant.service;

import com.assistant.model.ReplyAction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScenarioScheduler {

    private final ScenarioEngine scenarioEngine;
    private final MessageRouterService routerService;

    public ScenarioScheduler(ScenarioEngine scenarioEngine, MessageRouterService routerService) {
        this.scenarioEngine = scenarioEngine;
        this.routerService = routerService;
    }

    @Scheduled(fixedDelay = 10000)
    public void runScenarioDetection() {
        List<ReplyAction> actions = scenarioEngine.detect();
        for (ReplyAction action : actions) {
            routerService.routeScenarioAction(action);
        }
    }
}
package com.assistant.controller;

import com.assistant.model.ChatMessage;
import com.assistant.model.ReplyAction;
import com.assistant.service.MessageRouterService;
import com.assistant.service.ScenarioEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageRouterService routerService;
    private final ScenarioEngine scenarioEngine;

    public MessageController(MessageRouterService routerService, ScenarioEngine scenarioEngine) {
        this.routerService = routerService;
        this.scenarioEngine = scenarioEngine;
    }

    @PostMapping
    public ResponseEntity<ReplyAction> sendMessage(@RequestBody ChatMessage message) {
        if (message.getContent() == null || message.getContent().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ReplyAction action = routerService.routeMessage(message);
        return ResponseEntity.ok(action);
    }

    @PostMapping("/trigger")
    public ResponseEntity<ReplyAction> triggerScenario(@RequestBody Map<String, Object> trigger) {
        String scenarioId = (String) trigger.get("scenarioId");
        if (scenarioId == null) {
            return ResponseEntity.badRequest().build();
        }

        ReplyAction action = scenarioEngine.trigger(scenarioId);
        if (action == null) {
            return ResponseEntity.notFound().build();
        }

        routerService.routeScenarioAction(action);
        return ResponseEntity.ok(action);
    }
}

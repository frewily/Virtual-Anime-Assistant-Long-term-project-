package com.assistant.controller;

import com.assistant.service.MessageRouterService;
import com.assistant.websocket.DesktopClientHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private final DesktopClientHandler desktopHandler;
    private final MessageRouterService routerService;
    private final ObjectMapper objectMapper;

    public AvatarController(DesktopClientHandler desktopHandler,
            MessageRouterService routerService,
            ObjectMapper objectMapper) {
        this.desktopHandler = desktopHandler;
        this.routerService = routerService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/express")
    public ResponseEntity<Map<String, Object>> setExpression(@RequestBody Map<String, String> body) {
        String expression = body.get("expression");
        if (expression == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "action");
        payload.put("expression", expression);

        try {
            desktopHandler.broadcast(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("expression", expression);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/motion")
    public ResponseEntity<Map<String, Object>> playMotion(@RequestBody Map<String, String> body) {
        String motion = body.get("motion");
        if (motion == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "action");
        payload.put("motion", motion);

        try {
            desktopHandler.broadcast(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("motion", motion);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/speak")
    public ResponseEntity<Map<String, Object>> speak(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        String expression = body.getOrDefault("expression", "idle");
        String motion = body.getOrDefault("motion", "idle");

        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        routerService.sendToDesktop(text, expression, motion);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("text", text);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connectedClients", desktopHandler.getClientCount());
        status.put("wsEndpoint", "/ws/avatar");
        return ResponseEntity.ok(status);
    }
}
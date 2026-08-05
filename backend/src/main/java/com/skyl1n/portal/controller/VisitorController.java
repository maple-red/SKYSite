package com.skyl1n.portal.controller;

import com.skyl1n.portal.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping("/visit")
    public ResponseEntity<Map<String, Object>> recordVisit(HttpServletRequest request) {
        String sessionId = request.getHeader("x-session-id");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        visitorService.recordVisit(sessionId, request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "totalVisitors", visitorService.getTotalVisitors(),
                "onlineUsers", visitorService.getOnlineUsers()
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalVisitors", visitorService.getTotalVisitors(),
                "onlineUsers", visitorService.getOnlineUsers()
        ));
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(HttpServletRequest request) {
        String sessionId = request.getHeader("x-session-id");
        visitorService.heartbeat(sessionId);
        return ResponseEntity.ok(Map.of("onlineUsers", visitorService.getOnlineUsers()));
    }
}

package com.skyl1n.portal.controller;

import com.skyl1n.portal.entity.Moment;
import com.skyl1n.portal.service.MomentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moments")
public class MomentController {

    private final MomentService momentService;

    public MomentController(MomentService momentService) {
        this.momentService = momentService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll(HttpServletRequest request) {
        String sessionId = request.getHeader("x-session-id");
        return momentService.getAllMoments(sessionId);
    }

    @PostMapping
    public ResponseEntity<Moment> create(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Moment moment = momentService.createMoment(content);
        return ResponseEntity.status(201).body(moment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean ok = momentService.deleteMoment(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id, HttpServletRequest request) {
        String sessionId = request.getHeader("x-session-id");
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "缺少 session"));
        }
        Map<String, Object> result = momentService.toggleLike(id, sessionId);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }
}

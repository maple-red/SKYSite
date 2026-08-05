package com.skyl1n.portal.controller;

import com.skyl1n.portal.entity.Message;
import com.skyl1n.portal.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getAll() {
        return messageService.getAllMessages();
    }

    @PostMapping
    public ResponseEntity<Message> create(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "");
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Message msg = messageService.createMessage(name, content);
        return ResponseEntity.status(201).body(msg);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean ok = messageService.deleteMessage(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("success", true));
    }
}

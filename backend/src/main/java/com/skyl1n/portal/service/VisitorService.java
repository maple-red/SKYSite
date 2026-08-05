package com.skyl1n.portal.service;

import com.skyl1n.portal.entity.Visitor;
import com.skyl1n.portal.repository.VisitorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final Map<String, Long> onlineUsers = new ConcurrentHashMap<>();

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public long getTotalVisitors() {
        return visitorRepository.count();
    }

    public int getOnlineUsers() {
        long now = System.currentTimeMillis();
        onlineUsers.entrySet().removeIf(e -> now - e.getValue() > 60_000);
        return onlineUsers.size();
    }

    public Visitor recordVisit(String sessionId, String ip, String userAgent) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        Visitor visitor = visitorRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Visitor v = new Visitor();
                    v.setSessionId(sessionId);
                    v.setIp(ip);
                    v.setUserAgent(userAgent);
                    v.setFirstVisit(LocalDateTime.now());
                    return v;
                });
        visitor.setLastVisit(LocalDateTime.now());
        visitorRepository.save(visitor);

        onlineUsers.put(sessionId, System.currentTimeMillis());
        return visitor;
    }

    public void heartbeat(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            onlineUsers.put(sessionId, System.currentTimeMillis());
        }
    }
}

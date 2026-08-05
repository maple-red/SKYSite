package com.skyl1n.portal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sessionId;

    private String ip;
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime firstVisit = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastVisit = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getFirstVisit() { return firstVisit; }
    public void setFirstVisit(LocalDateTime firstVisit) { this.firstVisit = firstVisit; }
    public LocalDateTime getLastVisit() { return lastVisit; }
    public void setLastVisit(LocalDateTime lastVisit) { this.lastVisit = lastVisit; }
}

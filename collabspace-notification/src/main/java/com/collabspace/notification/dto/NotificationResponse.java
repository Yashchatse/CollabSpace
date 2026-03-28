package com.collabspace.notification.dto;

import com.collabspace.notification.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.message = n.getMessage();
        this.type = n.getType().name();
        this.isRead = n.isRead();
        this.createdAt = n.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
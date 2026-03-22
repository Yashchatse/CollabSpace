package com.collabspace.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_activity")
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taskId;
    private String userEmail;
    private String action;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
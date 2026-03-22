package com.collabspace.project.dto;
import com.collabspace.project.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Long columnId;
    private String assigneeEmail;
    private LocalDate dueDate;
    private String priority;
    private String createdBy;
    private LocalDateTime createdAt;
    public TaskResponse(Task t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.description = t.getDescription();
        this.columnId = t.getColumnId();
        this.assigneeEmail = t.getAssigneeEmail();
        this.dueDate = t.getDueDate();
        this.priority = t.getPriority().name();
        this.createdBy = t.getCreatedBy();
        this.createdAt = t.getCreatedAt();
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getColumnId() { return columnId; }
    public String getAssigneeEmail() { return assigneeEmail; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
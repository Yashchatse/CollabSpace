package com.collabspace.project.dto;
import java.time.LocalDate;
public class TaskRequest {
    private String title;
    private String description;
    private String assigneeEmail;
    private LocalDate dueDate;
    private String priority;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAssigneeEmail() { return assigneeEmail; }
    public void setAssigneeEmail(String assigneeEmail) { this.assigneeEmail = assigneeEmail; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
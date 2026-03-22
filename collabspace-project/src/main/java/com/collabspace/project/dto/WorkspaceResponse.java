package com.collabspace.project.dto;
import com.collabspace.project.entity.Workspace;
import java.time.LocalDateTime;
public class WorkspaceResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private String planType;
    private LocalDateTime createdAt;
    public WorkspaceResponse(Workspace w) {
        this.id = w.getId();
        this.name = w.getName();
        this.description = w.getDescription();
        this.ownerEmail = w.getOwnerEmail();
        this.planType = w.getPlanType().name();
        this.createdAt = w.getCreatedAt();
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getPlanType() { return planType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
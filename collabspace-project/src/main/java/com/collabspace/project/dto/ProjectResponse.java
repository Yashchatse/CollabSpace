package com.collabspace.project.dto;
import com.collabspace.project.entity.Project;
import java.time.LocalDateTime;
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private Long workspaceId;
    private LocalDateTime createdAt;
    public ProjectResponse(Project p) {
        this.id = p.getId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.workspaceId = p.getWorkspaceId();
        this.createdAt = p.getCreatedAt();
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getWorkspaceId() { return workspaceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
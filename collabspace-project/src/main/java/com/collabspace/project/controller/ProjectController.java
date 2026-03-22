package com.collabspace.project.controller;

import com.collabspace.project.dto.ProjectResponse;
import com.collabspace.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @PathVariable Long workspaceId) {
        return ResponseEntity.ok(projectService.getProjects(workspaceId));
    }
}
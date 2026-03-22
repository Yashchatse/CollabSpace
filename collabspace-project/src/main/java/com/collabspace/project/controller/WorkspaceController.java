package com.collabspace.project.controller;

import com.collabspace.project.dto.*;
import com.collabspace.project.service.ProjectService;
import com.collabspace.project.service.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final ProjectService projectService;

    public WorkspaceController(WorkspaceService workspaceService,
                                ProjectService projectService) {
        this.workspaceService = workspaceService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(
            @RequestBody WorkspaceRequest request,
            Authentication auth) {
        return ResponseEntity.ok(workspaceService.createWorkspace(request, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces(Authentication auth) {
        return ResponseEntity.ok(workspaceService.getMyWorkspaces(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getWorkspace(id));
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<String> invite(
            @PathVariable Long id,
            @RequestParam String email) {
        workspaceService.inviteMember(id, email);
        return ResponseEntity.ok("Member invited successfully");
    }

    @PostMapping("/{id}/projects")
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable Long id,
            @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(id, request));
    }
}
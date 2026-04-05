package com.collabspace.project.controller;

import com.collabspace.project.dto.ProjectResponse;
import com.collabspace.project.entity.BoardColumn;
import com.collabspace.project.repository.BoardColumnRepository;
import com.collabspace.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final BoardColumnRepository columnRepository;

    public ProjectController(ProjectService projectService,
                             BoardColumnRepository columnRepository) {
        this.projectService = projectService;
        this.columnRepository = columnRepository;
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

    @GetMapping("/{id}/columns")
    public ResponseEntity<List<Long>> getColumnIds(@PathVariable Long id) {
        return ResponseEntity.ok(
            columnRepository.findByProjectIdOrderByPosition(id)
                .stream()
                .map(BoardColumn::getId)
                .collect(Collectors.toList())
        );
    }
}
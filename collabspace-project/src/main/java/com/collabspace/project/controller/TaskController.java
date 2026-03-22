package com.collabspace.project.controller;

import com.collabspace.project.dto.MoveTaskRequest;
import com.collabspace.project.dto.TaskRequest;
import com.collabspace.project.dto.TaskResponse;
import com.collabspace.project.entity.TaskActivity;
import com.collabspace.project.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/column/{columnId}")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long columnId,
            @RequestBody TaskRequest request,
            Authentication auth) {
        return ResponseEntity.ok(taskService.createTask(columnId, request, auth.getName()));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskRequest request,
            Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, auth.getName()));
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskResponse> moveTask(
            @PathVariable Long taskId,
            @RequestBody MoveTaskRequest request,
            Authentication auth) {
        return ResponseEntity.ok(taskService.moveTask(taskId, request, auth.getName()));
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<TaskResponse>> getTasksByColumn(
            @PathVariable Long columnId) {
        return ResponseEntity.ok(taskService.getTasksByColumn(columnId));
    }

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<String> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        return ResponseEntity.ok(taskService.uploadAttachment(taskId, file, auth.getName()));
    }

    @GetMapping("/{taskId}/activity")
    public ResponseEntity<List<TaskActivity>> getActivity(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getActivity(taskId));
    }
}
package com.collabspace.project.service;

import com.cloudinary.Cloudinary;	
import com.cloudinary.utils.ObjectUtils;
import com.collabspace.project.dto.MoveTaskRequest;
import com.collabspace.project.dto.TaskRequest;
import com.collabspace.project.dto.TaskResponse;
import com.collabspace.project.entity.BoardColumn;
import com.collabspace.project.entity.Task;
import com.collabspace.project.entity.TaskActivity;
import com.collabspace.project.entity.TaskAttachment;
import com.collabspace.project.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskActivityRepository activityRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final BoardColumnRepository columnRepository;
    private final Cloudinary cloudinary;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskService(TaskRepository taskRepository,
                       TaskActivityRepository activityRepository,
                       TaskAttachmentRepository attachmentRepository,
                       BoardColumnRepository columnRepository,
                       Cloudinary cloudinary,
                       SimpMessagingTemplate messagingTemplate) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.attachmentRepository = attachmentRepository;
        this.columnRepository = columnRepository;
        this.cloudinary = cloudinary;
        this.messagingTemplate = messagingTemplate;
    }

    public TaskResponse createTask(Long columnId, TaskRequest request, String createdBy) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setColumnId(columnId);
        task.setAssigneeEmail(request.getAssigneeEmail());
        task.setDueDate(request.getDueDate());
        task.setCreatedBy(createdBy);

        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        }

        task = taskRepository.save(task);
        logActivity(task.getId(), createdBy, "Created task: " + task.getTitle());

        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        notifyBoard(column.getProjectId(), "TASK_CREATED");

        return new TaskResponse(task);
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getAssigneeEmail() != null) task.setAssigneeEmail(request.getAssigneeEmail());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        }

        task = taskRepository.save(task);
        logActivity(taskId, userEmail, "Updated task");

        BoardColumn column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new RuntimeException("Column not found"));
        notifyBoard(column.getProjectId(), "TASK_UPDATED");

        return new TaskResponse(task);
    }

    public TaskResponse moveTask(Long taskId, MoveTaskRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

//        Long oldColumnId = task.getColumnId();
        task.setColumnId(request.getTargetColumnId());
        task = taskRepository.save(task);

        logActivity(taskId, userEmail, "Moved task to column " + request.getTargetColumnId());

        BoardColumn column = columnRepository.findById(request.getTargetColumnId())
                .orElseThrow(() -> new RuntimeException("Column not found"));
        notifyBoard(column.getProjectId(), "TASK_MOVED");

        return new TaskResponse(task);
    }

    public List<TaskResponse> getTasksByColumn(Long columnId) {
        return taskRepository.findByColumnId(columnId)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public String uploadAttachment(Long taskId, MultipartFile file,
                                    String userEmail) throws IOException {
    	
    	@SuppressWarnings("unchecked")
    	Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "collabspace/attachments")
        );

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileUrl((String) uploadResult.get("secure_url"));
        attachment.setFileName(file.getOriginalFilename());
        attachment.setUploadedBy(userEmail);
        attachmentRepository.save(attachment);

        logActivity(taskId, userEmail, "Added attachment: " + file.getOriginalFilename());
        return (String) uploadResult.get("secure_url");
    }

    public List<TaskActivity> getActivity(Long taskId) {
        return activityRepository.findByTaskIdOrderByTimestampDesc(taskId);
    }

    private void logActivity(Long taskId, String userEmail, String action) {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setUserEmail(userEmail);
        activity.setAction(action);
        activityRepository.save(activity);
    }

    private void notifyBoard(Long projectId, String event) {
        messagingTemplate.convertAndSend("/topic/board/" + projectId, event);
    }
}
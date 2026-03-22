package com.collabspace.project.service;

import com.collabspace.project.dto.ProjectRequest;
import com.collabspace.project.dto.ProjectResponse;
import com.collabspace.project.entity.BoardColumn;
import com.collabspace.project.entity.Project;
import com.collabspace.project.repository.BoardColumnRepository;
import com.collabspace.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final BoardColumnRepository columnRepository;

    public ProjectService(ProjectRepository projectRepository,
                          BoardColumnRepository columnRepository) {
        this.projectRepository = projectRepository;
        this.columnRepository = columnRepository;
    }

    public ProjectResponse createProject(Long workspaceId, ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setWorkspaceId(workspaceId);
        project = projectRepository.save(project);

        createDefaultColumns(project.getId());
        return new ProjectResponse(project);
    }

    private void createDefaultColumns(Long projectId) {
        String[] defaults = {"To Do", "In Progress", "Done"};
        for (int i = 0; i < defaults.length; i++) {
            BoardColumn col = new BoardColumn();
            col.setName(defaults[i]);
            col.setProjectId(projectId);
            col.setPosition(i);
            columnRepository.save(col);
        }
    }

    public List<ProjectResponse> getProjects(Long workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProject(Long id) {
        return new ProjectResponse(projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found")));
    }
}
package com.collabspace.project.repository;
import com.collabspace.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByColumnId(Long columnId);
    List<Task> findByAssigneeEmail(String assigneeEmail);
}
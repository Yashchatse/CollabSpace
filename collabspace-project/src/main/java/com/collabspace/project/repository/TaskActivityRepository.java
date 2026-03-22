package com.collabspace.project.repository;
import com.collabspace.project.entity.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTaskIdOrderByTimestampDesc(Long taskId);
}
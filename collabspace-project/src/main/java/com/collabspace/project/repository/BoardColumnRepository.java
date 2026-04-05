package com.collabspace.project.repository;

import com.collabspace.project.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByProjectIdOrderByPosition(Long projectId);
}
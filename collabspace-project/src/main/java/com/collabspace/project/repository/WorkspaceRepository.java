package com.collabspace.project.repository;
import com.collabspace.project.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwnerEmail(String ownerEmail);
}
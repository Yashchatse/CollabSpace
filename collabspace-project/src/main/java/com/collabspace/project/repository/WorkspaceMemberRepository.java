package com.collabspace.project.repository;
import com.collabspace.project.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);
    List<WorkspaceMember> findByMemberEmail(String memberEmail);
    Optional<WorkspaceMember> findByWorkspaceIdAndMemberEmail(Long workspaceId, String memberEmail);
    boolean existsByWorkspaceIdAndMemberEmail(Long workspaceId, String memberEmail);
}
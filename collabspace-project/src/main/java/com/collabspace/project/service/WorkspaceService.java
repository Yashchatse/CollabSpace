package com.collabspace.project.service;

import com.collabspace.project.dto.WorkspaceRequest;
import com.collabspace.project.dto.WorkspaceResponse;
import com.collabspace.project.entity.Workspace;
import com.collabspace.project.entity.WorkspaceMember;
import com.collabspace.project.repository.WorkspaceMemberRepository;
import com.collabspace.project.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository memberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
    }

    public WorkspaceResponse createWorkspace(WorkspaceRequest request, String ownerEmail) {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setOwnerEmail(ownerEmail);
        workspace = workspaceRepository.save(workspace);

        WorkspaceMember owner = new WorkspaceMember();
        owner.setWorkspaceId(workspace.getId());
        owner.setMemberEmail(ownerEmail);
        owner.setRole(WorkspaceMember.MemberRole.OWNER);
        memberRepository.save(owner);

        return new WorkspaceResponse(workspace);
    }

    public List<WorkspaceResponse> getMyWorkspaces(String email) {
        return workspaceRepository.findByOwnerEmail(email)
                .stream()
                .map(WorkspaceResponse::new)
                .collect(Collectors.toList());
    }

    public WorkspaceResponse getWorkspace(Long id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
        return new WorkspaceResponse(workspace);
    }

    public void inviteMember(Long workspaceId, String memberEmail) {
        if (memberRepository.existsByWorkspaceIdAndMemberEmail(workspaceId, memberEmail)) {
            throw new RuntimeException("Member already in workspace");
        }
        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspaceId(workspaceId);
        member.setMemberEmail(memberEmail);
        member.setRole(WorkspaceMember.MemberRole.MEMBER);
        memberRepository.save(member);
    }
}
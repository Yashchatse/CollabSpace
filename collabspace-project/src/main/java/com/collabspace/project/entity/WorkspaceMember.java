package com.collabspace.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workspace_members")
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private String memberEmail;

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.MEMBER;

    public enum MemberRole { OWNER, MEMBER, VIEWER }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) { this.role = role; }
}
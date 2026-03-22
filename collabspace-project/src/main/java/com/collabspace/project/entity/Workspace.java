package com.collabspace.project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "workspace")
public class Workspace {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	private String description;
	
	@Column(nullable = false)
	private String ownerEmail;
	
	@Enumerated(EnumType.STRING)
	private PlanType planType = PlanType.FREE;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	public enum PlanType {FREE, PRO}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public PlanType getPlanType() {
		return planType;
	}
	public void setPlanType(PlanType planType) {
		this.planType = planType;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
	
	
	


}

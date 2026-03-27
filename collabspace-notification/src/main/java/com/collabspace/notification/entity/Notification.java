package com.collabspace.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name= "notifications")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String recipientEmail;
	
	@Column(nullable = false)
	private String message;
	
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	
	private boolean isRead = false;
	private LocalDateTime createdAt = LocalDateTime.now();
	
	public enum NotificationType{
		TASK_ASSIGNED,
        WORKSPACE_INVITE,
        PAYMENT_SUCCESS,
        GENERAL
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}

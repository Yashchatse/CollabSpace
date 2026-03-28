package com.collabspace.notification.repository;

import com.collabspace.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
    List<Notification> findByRecipientEmailAndIsReadFalse(String email);
    long countByRecipientEmailAndIsReadFalse(String email);
}
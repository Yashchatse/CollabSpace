package com.collabspace.notification.service;

import com.collabspace.notification.dto.NotificationRequest;
import com.collabspace.notification.dto.NotificationResponse;
import com.collabspace.notification.entity.Notification;
import com.collabspace.notification.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                                EmailService emailService,
                                SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setMessage(request.getMessage());
        notification.setType(
            Notification.NotificationType.valueOf(request.getType()));
        notificationRepository.save(notification);

        // Push real-time notification via WebSocket
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + request.getRecipientEmail(),
            new NotificationResponse(notification)
        );

        // Send email
        emailService.sendEmail(
            request.getRecipientEmail(),
            "CollabSpace — " + request.getType(),
            request.getMessage()
        );
    }

    public List<NotificationResponse> getMyNotifications(String email) {
        return notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String email) {
        return notificationRepository
                .countByRecipientEmailAndIsReadFalse(email);
    }

    public void markAllRead(String email) {
        List<Notification> unread = notificationRepository
                .findByRecipientEmailAndIsReadFalse(email);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
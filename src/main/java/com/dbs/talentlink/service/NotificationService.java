package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.NotificationResponse;
import com.dbs.talentlink.dto.NotificationSummaryResponse;
import com.dbs.talentlink.entity.Notification;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(User recipient, String content, String linkUrl) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .content(content)
                .linkUrl(linkUrl)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public NotificationSummaryResponse getNotificationsForUser(User currentUser, Pageable pageable) {
        long unreadCount = notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
        Page<NotificationResponse> notifications = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(NotificationResponse::fromEntity);
        return new NotificationSummaryResponse(unreadCount, notifications);
    }

    @Transactional
    public void markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User cannot access this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
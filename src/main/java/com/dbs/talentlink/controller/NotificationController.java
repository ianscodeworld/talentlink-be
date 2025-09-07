package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.NotificationSummaryResponse;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSummaryResponse> getNotifications(
            @AuthenticationPrincipal User currentUser, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser, pageable));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User currentUser) {
        notificationService.markAsRead(notificationId, currentUser);
        return ResponseEntity.ok().build();
    }
}
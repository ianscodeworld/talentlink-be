package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Notification;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String content;
    private String linkUrl;
    private boolean isRead;
    private Instant createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .linkUrl(notification.getLinkUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
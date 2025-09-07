package com.dbs.talentlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class NotificationSummaryResponse {
    private long unreadCount;
    private Page<NotificationResponse> notifications;
}
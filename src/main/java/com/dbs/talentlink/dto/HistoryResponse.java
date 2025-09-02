package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.CandidateHistory;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HistoryResponse {
    private Long id;
    private String actionType;
    private String details;
    private Long createdById;
    private String createdByName;
    private Instant createdAt;

    public static HistoryResponse fromEntity(CandidateHistory history) {
        return HistoryResponse.builder()
                .id(history.getId())
                .actionType(history.getActionType())
                .details(history.getDetails())
                .createdById(history.getCreatedBy().getId())
                .createdByName(history.getCreatedBy().getName())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
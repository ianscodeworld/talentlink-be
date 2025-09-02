package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.model.DemandStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class DemandDetailResponse {
    private Long id;
    private String jobTitle;
    private String description;
    private int totalInterviewRounds;
    private DemandStatus status;
    private Long createdById;
    private String createdByName;
    private Instant createdAt;
    private List<CandidateResponse> candidates; // 包含候选人列表

    public static DemandDetailResponse fromEntity(Demand demand) {
        return DemandDetailResponse.builder()
                .id(demand.getId())
                .jobTitle(demand.getJobTitle())
                .description(demand.getDescription())
                .totalInterviewRounds(demand.getTotalInterviewRounds())
                .status(demand.getStatus())
                .createdById(demand.getCreatedBy().getId())
                .createdByName(demand.getCreatedBy().getName())
                .createdAt(demand.getCreatedAt())
                .candidates(demand.getCandidates().stream()
                        .map(CandidateResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

}
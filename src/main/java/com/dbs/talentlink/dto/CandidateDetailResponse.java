package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.model.CandidateStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CandidateDetailResponse {
    private Long id;
    private String name;
    private String resumeSummary;
    private int currentInterviewRound;
    private CandidateStatus status;
    private Long demandId;
    private String vendorName;
    private Instant createdAt;
    private List<FeedbackResponse> feedbacks;

    // --- 新增字段 ---
    private Integer totalRoundsOverride;

    public static CandidateDetailResponse fromEntity(Candidate candidate) {
        return CandidateDetailResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .resumeSummary(candidate.getResumeSummary())
                .currentInterviewRound(candidate.getCurrentInterviewRound())
                .status(candidate.getStatus())
                .demandId(candidate.getDemand().getId())
                .vendorName(candidate.getVendorName())
                .createdAt(candidate.getCreatedAt())
                .feedbacks(candidate.getFeedbacks().stream()
                        .map(FeedbackResponse::fromEntity)
                        .collect(Collectors.toList()))
                // --- 填充新增字段 ---
                .totalRoundsOverride(candidate.getTotalRoundsOverride())
                .build();
    }
}
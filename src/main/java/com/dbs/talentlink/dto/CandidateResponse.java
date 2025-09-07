package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.model.CandidateStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CandidateResponse {
    private Long id;
    private String name;
    private String resumeSummary;
    private int currentInterviewRound;
    private CandidateStatus status;
    private Long demandId;
    private String vendorName;
    private Instant createdAt;

    // --- 新增字段 ---
    private Integer totalRoundsOverride;

    public static CandidateResponse fromEntity(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .resumeSummary(candidate.getResumeSummary())
                .currentInterviewRound(candidate.getCurrentInterviewRound())
                .status(candidate.getStatus())
                .demandId(candidate.getDemand().getId())
                .vendorName(candidate.getVendorName())
                .createdAt(candidate.getCreatedAt())
                // --- 填充新增字段 ---
                .totalRoundsOverride(candidate.getTotalRoundsOverride())
                .build();
    }
}
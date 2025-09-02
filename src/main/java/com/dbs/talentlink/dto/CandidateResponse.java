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
    private Long vendorId;
    private String vendorName;
    private Instant createdAt;

    public static CandidateResponse fromEntity(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .resumeSummary(candidate.getResumeSummary())
                .currentInterviewRound(candidate.getCurrentInterviewRound())
                .status(candidate.getStatus())
                .demandId(candidate.getDemand().getId())
                .vendorId(candidate.getVendor().getId())
                .vendorName(candidate.getVendor().getCompanyName())
                .createdAt(candidate.getCreatedAt())
                .build();
    }
}
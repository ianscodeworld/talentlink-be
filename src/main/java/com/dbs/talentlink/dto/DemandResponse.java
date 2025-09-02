package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.model.DemandStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DemandResponse {
    private Long id;
    private String jobTitle;
    private String description;
    private int totalInterviewRounds;
    private DemandStatus status;
    private Long createdById;
    private String createdByName;
    private Instant createdAt;

    // A static factory method for easy conversion from Entity to DTO
    public static DemandResponse fromEntity(Demand demand) {
        return DemandResponse.builder()
                .id(demand.getId())
                .jobTitle(demand.getJobTitle())
                .description(demand.getDescription())
                .totalInterviewRounds(demand.getTotalInterviewRounds())
                .status(demand.getStatus())
                .createdById(demand.getCreatedBy().getId())
                .createdByName(demand.getCreatedBy().getName())
                .createdAt(demand.getCreatedAt())
                .build();
    }
}
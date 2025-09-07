package com.dbs.talentlink.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SquadSummaryDto {
    private Long squadId;
    private String squadName;
    private int totalPositions;
    private int filledPositions;
    private double progressPercentage;
    private String roleBreakdown;
}
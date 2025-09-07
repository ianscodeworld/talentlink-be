package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Squad;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SquadDetailResponse {
    private Long id;
    private String name;
    private int totalDemands;
    private int totalCandidates;
    private Map<String, Long> candidateStatusCounts; // e.g., {"SCREENING": 10, "INTERVIEW": 5}
    private List<DemandResponse> demands; // 关联的需求列表

    public static SquadDetailResponse fromEntity(Squad squad, Map<String, Long> statusCounts, List<DemandResponse> demandDtos) {
        return SquadDetailResponse.builder()
                .id(squad.getId())
                .name(squad.getName())
                .demands(demandDtos)
                .totalDemands(demandDtos.size())
                .candidateStatusCounts(statusCounts)
                .totalCandidates((int) statusCounts.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }
}
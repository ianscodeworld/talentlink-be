package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Demand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleDemandDto {
    private Long id;
    private String jobTitle;

    public static SimpleDemandDto fromEntity(Demand demand) {
        return SimpleDemandDto.builder()
                .id(demand.getId())
                .jobTitle(demand.getJobTitle())
                .build();
    }
}
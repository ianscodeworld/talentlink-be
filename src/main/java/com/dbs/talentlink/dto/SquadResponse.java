package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Squad;
import com.dbs.talentlink.model.SquadStatus; // 新增 import
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SquadResponse {
    private Long id;
    private String name;

    // --- 新增字段 ---
    private SquadStatus status;

    public static SquadResponse fromEntity(Squad squad) {
        return SquadResponse.builder()
                .id(squad.getId())
                .name(squad.getName())
                // --- 填充新增字段 ---
                .status(squad.getStatus())
                .build();
    }
}
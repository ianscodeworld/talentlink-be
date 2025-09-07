package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // 新增 import
import lombok.Data;

@Data
public class CheckDuplicateRequest {
    @NotBlank(message = "Candidate name cannot be blank")
    private String name;

    // --- 新增字段 ---
    @NotNull(message = "Demand ID is required for context")
    private Long demandId;
}
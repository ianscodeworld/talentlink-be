package com.dbs.talentlink.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoundsRequest {
    @NotNull(message = "Total rounds cannot be null")
    @Min(value = 1, message = "There must be at least 1 interview round")
    private Integer totalRounds;
}
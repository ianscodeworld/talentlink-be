package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSquadRequest {
    @NotBlank(message = "Squad name cannot be blank")
    private String name;
}
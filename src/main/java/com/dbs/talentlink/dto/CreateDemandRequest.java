package com.dbs.talentlink.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CreateDemandRequest {
    @NotBlank(message = "Job title cannot be blank")
    private String jobTitle;

    private String description;

    @Min(value = 1, message = "There must be at least one interview round")
    private int totalInterviewRounds;

    private List<String> specialties;

    private Long squadId;
}
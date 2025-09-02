package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignInterviewerRequest {
    @NotNull(message = "Interviewer ID cannot be null")
    private Long interviewerId;
}
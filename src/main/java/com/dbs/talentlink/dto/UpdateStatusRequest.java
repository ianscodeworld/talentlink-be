package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.CandidateStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "Status cannot be null")
    private CandidateStatus status;
}
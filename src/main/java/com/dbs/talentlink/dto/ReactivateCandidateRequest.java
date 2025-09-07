// com/dbs/talentlink/dto/ReactivateCandidateRequest.java
package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactivateCandidateRequest {
    @NotNull
    private Long candidateId;
    @NotNull
    private Long targetDemandId;
}
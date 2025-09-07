// com/dbs/talentlink/dto/UpdateSquadStatusRequest.java
package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.SquadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSquadStatusRequest {
    @NotNull(message = "Status cannot be null")
    private SquadStatus status;
}
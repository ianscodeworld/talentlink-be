// com/dbs/talentlink/dto/UpdateDemandStatusRequest.java
package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.DemandStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDemandStatusRequest {
    @NotNull(message = "Status cannot be null")
    private DemandStatus status;
}
// com/dbs/talentlink/dto/UpdateDemandRequest.java
package com.dbs.talentlink.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class UpdateDemandRequest {
    // Job Title 通常创建后不应修改，故此处不包含
    private String description;

    @Min(value = 1, message = "There must be at least one interview round")
    private Integer totalInterviewRounds;

    @Min(value = 1, message = "Required positions must be at least 1")
    private Integer requiredPositions;

    private List<String> specialties;

    private Long squadId;
}
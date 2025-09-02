// com/dbs/talentlink/dto/UpdateSpecialtyRequest.java
package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class UpdateSpecialtyRequest {
    @NotEmpty(message = "Specialty list cannot be empty")
    private List<String> specialties; // e.g., ["JAVA", "REACT"]
}
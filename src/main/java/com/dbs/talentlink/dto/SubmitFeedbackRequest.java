package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.Recommendation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitFeedbackRequest {
    @NotBlank(message = "Evaluation text cannot be blank")
    private String evaluationText;

    @NotNull(message = "Recommendation cannot be null")
    private Recommendation recommendation;
}
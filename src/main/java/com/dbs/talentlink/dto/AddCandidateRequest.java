package com.dbs.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCandidateRequest {
    @NotBlank(message = "Candidate name cannot be blank")
    private String name;

    @NotNull(message = "Vendor ID cannot be null")
    private Long vendorId;

    private String resumeSummary;

    // --- 新增字段 ---
    private String gender;
    private String skillset;
    private String seniority;
    private String relatedWorkingExperience;
    private String onboardingTime;
    private String skillHighlights;
    private String englishCapability;
    private String internalInterviewFeedback;
    private String onlineCodingResult;
}
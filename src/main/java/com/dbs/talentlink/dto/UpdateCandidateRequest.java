package com.dbs.talentlink.dto;

import lombok.Data;

@Data
public class UpdateCandidateRequest {
    // 允许修改候选人姓名和供应商
    private String name;
    private String vendorName;

    // 允许修改所有详细信息
    private String resumeSummary;
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
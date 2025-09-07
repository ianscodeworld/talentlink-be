package com.dbs.talentlink.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class HmDashboardResponse {
    // --- 新增字段 ---
    private List<SquadSummaryDto> squadsSummary;

    // --- 已有字段 ---
    private Map<String, Long> candidateFunnel;
    private List<InterviewerWorkloadDto> weeklyInterviewerWorkload;
    private List<SimpleDemandDto> demandsWithoutCandidates;
    private List<FeedbackResponse> recentFeedbacks;
}
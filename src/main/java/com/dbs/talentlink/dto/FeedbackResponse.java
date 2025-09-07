package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.Feedback;
import com.dbs.talentlink.model.Recommendation;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private String evaluationText;
    private Recommendation recommendation;
    private int interviewRound;
    private Long interviewerId;
    private String interviewerName;
    private Instant createdAt;

    // --- 新增的上下文信息字段 ---
    private Long demandId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;


    public static FeedbackResponse fromEntity(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .evaluationText(feedback.getEvaluationText())
                .recommendation(feedback.getRecommendation())
                .interviewRound(feedback.getInterviewRound())
                .interviewerId(feedback.getInterviewer().getId())
                .interviewerName(feedback.getInterviewer().getName())
                .createdAt(feedback.getCreatedAt())
                // --- 关联查询并填充新增字段 ---
                .demandId(feedback.getCandidate().getDemand().getId())
                .jobTitle(feedback.getCandidate().getDemand().getJobTitle())
                .candidateId(feedback.getCandidate().getId())
                .candidateName(feedback.getCandidate().getName())
                .build();
    }
}
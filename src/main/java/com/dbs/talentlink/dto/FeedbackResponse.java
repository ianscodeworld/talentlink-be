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

    public static FeedbackResponse fromEntity(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .evaluationText(feedback.getEvaluationText())
                .recommendation(feedback.getRecommendation())
                .interviewRound(feedback.getInterviewRound())
                .interviewerId(feedback.getInterviewer().getId())
                .interviewerName(feedback.getInterviewer().getName())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
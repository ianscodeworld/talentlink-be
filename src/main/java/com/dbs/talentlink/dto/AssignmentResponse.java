package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.InterviewAssignment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentResponse {
    private Long assignmentId;
    private Long candidateId;
    private String candidateName;
    private Long interviewerId;
    private String interviewerName;
    private int interviewRound;
    private boolean isCompleted;

    public static AssignmentResponse fromEntity(InterviewAssignment assignment) {
        return AssignmentResponse.builder()
                .assignmentId(assignment.getId())
                .candidateId(assignment.getCandidate().getId())
                .candidateName(assignment.getCandidate().getName())
                .interviewerId(assignment.getInterviewer().getId())
                .interviewerName(assignment.getInterviewer().getName())
                .interviewRound(assignment.getInterviewRound())
                .isCompleted(assignment.isCompleted())
                .build();
    }
}
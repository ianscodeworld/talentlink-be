package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.InterviewAssignment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyAssignmentResponse {
    private Long assignmentId;
    private Long candidateId;
    private String candidateName;
    private String resumeSummary;
    private int interviewRound;
    private Long demandId;
    private String jobTitle;

    public static MyAssignmentResponse fromEntity(InterviewAssignment assignment) {
        return MyAssignmentResponse.builder()
                .assignmentId(assignment.getId())
                .candidateId(assignment.getCandidate().getId())
                .candidateName(assignment.getCandidate().getName())
                .resumeSummary(assignment.getCandidate().getResumeSummary())
                .interviewRound(assignment.getInterviewRound())
                .demandId(assignment.getCandidate().getDemand().getId())
                .jobTitle(assignment.getCandidate().getDemand().getJobTitle())
                .build();
    }
}
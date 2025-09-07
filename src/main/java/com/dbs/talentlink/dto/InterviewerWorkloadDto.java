package com.dbs.talentlink.dto;

import com.dbs.talentlink.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewerWorkloadDto {
    private Long interviewerId;
    private String interviewerName;
    private Long feedbackCount;
}
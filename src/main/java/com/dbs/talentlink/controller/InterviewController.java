package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.MyAssignmentResponse;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/my-assignments")
    @PreAuthorize("hasAnyRole('INTERVIEWER', 'HM')")
    public ResponseEntity<Page<MyAssignmentResponse>> getMyAssignments(
            @AuthenticationPrincipal User currentUser, Pageable pageable) {
        Page<MyAssignmentResponse> assignments = interviewService.findMyAssignments(currentUser, pageable);
        return ResponseEntity.ok(assignments);
    }
}
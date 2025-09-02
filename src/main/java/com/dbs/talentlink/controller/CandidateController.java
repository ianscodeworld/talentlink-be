package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.*;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.CandidateHistoryService;
import com.dbs.talentlink.service.CandidateService;
import com.dbs.talentlink.service.InterviewService; // 注意 service 变更为 InterviewService
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final InterviewService interviewService;
    private final CandidateHistoryService historyService;


    @PostMapping("/{candidateId}/feedback")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable Long candidateId,
            @Valid @RequestBody SubmitFeedbackRequest request,
            @AuthenticationPrincipal User currentUser) {
        interviewService.submitFeedback(candidateId, request, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{candidateId}")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<CandidateDetailResponse> getCandidateById(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal User currentUser) {
        CandidateDetailResponse candidateDetail = candidateService.findCandidateByIdForOwner(candidateId, currentUser);
        return ResponseEntity.ok(candidateDetail);
    }

    @PutMapping("/{candidateId}/status")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<CandidateResponse> updateCandidateStatus(
            @PathVariable Long candidateId,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal User currentUser) {
        CandidateResponse updatedCandidate = candidateService.updateCandidateStatus(candidateId, request, currentUser);
        return ResponseEntity.ok(updatedCandidate);
    }


    @GetMapping("/{candidateId}/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<HistoryResponse>> getCandidateHistory(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal User currentUser,
            Pageable pageable) {
        Page<HistoryResponse> history = historyService.findHistoryForCandidate(candidateId, currentUser, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{candidateId}/vendor-email")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<EmailSummaryResponse> getVendorEmailSummary(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal User currentUser) {
        String emailContent = candidateService.generateVendorEmailSummary(candidateId, currentUser);
        return ResponseEntity.ok(new EmailSummaryResponse(emailContent));
    }
}
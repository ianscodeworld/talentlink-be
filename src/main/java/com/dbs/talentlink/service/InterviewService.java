package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.MyAssignmentResponse;
import com.dbs.talentlink.entity.*;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.CandidateRepository;
import com.dbs.talentlink.repository.FeedbackRepository;
import com.dbs.talentlink.repository.InterviewAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dbs.talentlink.dto.SubmitFeedbackRequest;
import com.dbs.talentlink.model.CandidateStatus;
import com.dbs.talentlink.model.Recommendation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewAssignmentRepository assignmentRepository;
    private final CandidateRepository candidateRepository;      // 新增依赖
    private final FeedbackRepository feedbackRepository;          // 新增依赖
    private final CandidateHistoryService historyService;
    private final NotificationService notificationService; // 新增依赖


    @Transactional(readOnly = true)
    public Page<MyAssignmentResponse> findMyAssignments(User currentUser, Pageable pageable) {
        return assignmentRepository.findByInterviewerIdAndIsCompletedFalse(currentUser.getId(), pageable)
                .map(MyAssignmentResponse::fromEntity);
    }

    @Transactional
    public void submitFeedback(Long candidateId, SubmitFeedbackRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));

        String historySummary; // 用于构建日志摘要

        if (currentUser.getRole() == Role.HM) {
            if (request.getInterviewerNameOverride() == null || request.getInterviewerNameOverride().isBlank()) {
                throw new IllegalArgumentException("HM must provide an interviewer_name_override when submitting feedback.");
            }
            historySummary = String.format("Feedback submitted by %s on behalf of '%s': %s.",
                    currentUser.getName(), request.getInterviewerNameOverride(), request.getRecommendation());

        } else { // Interviewer
            // ... (权限和重复提交校验逻辑不变)
            Set<Specialty> userSpecialties = currentUser.getSpecialties();
            Set<Specialty> demandSpecialties = candidate.getDemand().getSpecialties();
            boolean isFullstack = userSpecialties.stream().anyMatch(s -> "FULLSTACK".equalsIgnoreCase(s.getName()));
            if (!isFullstack && Collections.disjoint(userSpecialties, demandSpecialties)) {
                throw new AccessDeniedException("Your specialty does not match the requirement for this demand.");
            }
            int currentRound = candidate.getCurrentInterviewRound();
            if (feedbackRepository.existsByCandidateIdAndInterviewerIdAndInterviewRound(candidate.getId(), currentUser.getId(), currentRound)) {
                throw new IllegalStateException("You have already submitted feedback for this candidate for round " + currentRound);
            }
            historySummary = String.format("Feedback submitted by %s: %s.", currentUser.getName(), request.getRecommendation());
        }

        // 保存反馈记录 (逻辑不变)
        Feedback feedback = Feedback.builder()
                .candidate(candidate)
                .interviewer(currentUser)
                .interviewRound(candidate.getCurrentInterviewRound())
                .evaluationText(request.getEvaluationText())
                .recommendation(request.getRecommendation())
                .build();
        feedbackRepository.save(feedback);

        // --- 核心修改点 ---
        // 将 feedback 的详细内容附加到历史日志中
        String fullHistoryDetails = historySummary + "\n\n--- Evaluation ---\n" + request.getEvaluationText();

        // 根据反馈结果更新候选人状态
        if (request.getRecommendation() == Recommendation.FAIL) {
            candidate.setStatus(CandidateStatus.REJECTED);
            historyService.logAction(candidate, currentUser, "FEEDBACK_FAIL", fullHistoryDetails);
        } else { // PASS
            int totalRounds = candidate.getTotalRoundsOverride() != null
                    ? candidate.getTotalRoundsOverride()
                    : candidate.getDemand().getTotalInterviewRounds();
            boolean isFinalRound = candidate.getCurrentInterviewRound() >= totalRounds;

            if (isFinalRound) {
                candidate.setStatus(CandidateStatus.FINALIST);
                historyService.logAction(candidate, currentUser, "FEEDBACK_PASS_FINAL", fullHistoryDetails);
            } else {
                candidate.setStatus(CandidateStatus.PASSED_WAITING_FOR_INTERVIEW);
                candidate.setCurrentInterviewRound(candidate.getCurrentInterviewRound() + 1);
                historyService.logAction(candidate, currentUser, "FEEDBACK_PASS", fullHistoryDetails);
            }
        }
        candidateRepository.save(candidate); // 保存候选人的状态变更

        // --- 新增：发送通知给 HM ---
        User demandOwner = candidate.getDemand().getCreatedBy();
        String content = String.format("Interviewer %s has submitted feedback for candidate %s.",
                currentUser.getName(),
                candidate.getName());
        String linkUrl = String.format("/demands/%d/candidates/%d",
                candidate.getDemand().getId(),
                candidate.getId());

        notificationService.createNotification(demandOwner, content, linkUrl);
    }
}
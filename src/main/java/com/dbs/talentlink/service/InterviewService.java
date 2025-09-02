package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.MyAssignmentResponse;
import com.dbs.talentlink.entity.*;
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

    @Transactional(readOnly = true)
    public Page<MyAssignmentResponse> findMyAssignments(User currentUser, Pageable pageable) {
        return assignmentRepository.findByInterviewerIdAndIsCompletedFalse(currentUser.getId(), pageable)
                .map(MyAssignmentResponse::fromEntity);
    }

    @Transactional
    public void submitFeedback(Long candidateId, SubmitFeedbackRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));

        // 1. 新增权限校验：检查面试官与需求的专业技能是否匹配
        Set<Specialty> userSpecialties = currentUser.getSpecialties();
        Set<Specialty> demandSpecialties = candidate.getDemand().getSpecialties();

        boolean isFullstack = userSpecialties.stream()
                .anyMatch(s -> "FULLSTACK".equalsIgnoreCase(s.getName()));

        // 如果不是FULLSTACK，则检查技能是否有交集
        if (!isFullstack && Collections.disjoint(userSpecialties, demandSpecialties)) {
            throw new AccessDeniedException("Your specialty does not match the requirement for this demand.");
        }

        // 2. 移除旧的 assignment 查询和更新逻辑

        // 3. 保存反馈记录 (逻辑不变)
        Feedback feedback = Feedback.builder()
                .candidate(candidate)
                .interviewer(currentUser) // interviewer 直接取当前用户
                .interviewRound(candidate.getCurrentInterviewRound())
                .evaluationText(request.getEvaluationText())
                .recommendation(request.getRecommendation())
                .build();
        feedbackRepository.save(feedback);

        // 4. 根据反馈结果更新候选人状态 (逻辑不变)
        if (request.getRecommendation() == Recommendation.FAIL) {
            candidate.setStatus(CandidateStatus.REJECTED);
            historyService.logAction(candidate, currentUser, "FEEDBACK_FAIL",
                    "Round " + candidate.getCurrentInterviewRound() + " failed. Candidate rejected.");
        } else { // PASS
            boolean isFinalRound = candidate.getCurrentInterviewRound() >= candidate.getDemand().getTotalInterviewRounds();
            if (isFinalRound) {
                candidate.setStatus(CandidateStatus.FINALIST);
                historyService.logAction(candidate, currentUser, "FEEDBACK_PASS_FINAL",
                        "Final round " + candidate.getCurrentInterviewRound() + " passed. Candidate is now a finalist.");
            } else {
                candidate.setStatus(CandidateStatus.PASSED_WAITING_FOR_INTERVIEW);
                candidate.setCurrentInterviewRound(candidate.getCurrentInterviewRound() + 1);
                historyService.logAction(candidate, currentUser, "FEEDBACK_PASS",
                        "Round " + (candidate.getCurrentInterviewRound() - 1) + " passed. Advanced to round " + candidate.getCurrentInterviewRound() + ".");
            }
        }
        candidateRepository.save(candidate);
    }
}
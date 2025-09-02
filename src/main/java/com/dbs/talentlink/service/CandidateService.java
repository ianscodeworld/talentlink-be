package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.*;
import com.dbs.talentlink.entity.*;
import com.dbs.talentlink.model.CandidateStatus;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final DemandRepository demandRepository;
    private final VendorRepository vendorRepository;
    private final CandidateHistoryService historyService;
    private final UserRepository userRepository; // 新增依赖
    private final InterviewAssignmentRepository assignmentRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional(readOnly = true)
    public String generateVendorEmailSummary(Long candidateId, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));

        // 权限校验: 只有该需求的 HM 才能生成邮件
        if (!candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to generate summaries for this candidate");
        }

        List<Feedback> feedbacks = feedbackRepository.findByCandidateIdOrderByInterviewRoundAsc(candidateId);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Dear Vendor,\n\nHere is the interview feedback summary for candidate: %s.\n\n", candidate.getName()));

        if (feedbacks.isEmpty()) {
            sb.append("No feedback has been submitted yet.\n");
        } else {
            for (Feedback feedback : feedbacks) {
                sb.append(String.format("--- Round %d Feedback from %s ---\n", feedback.getInterviewRound(), feedback.getInterviewer().getName()));
                sb.append(String.format("Recommendation: %s\n", feedback.getRecommendation()));
                sb.append("Comments:\n");
                sb.append(feedback.getEvaluationText());
                sb.append("\n\n");
            }
        }
        sb.append("Best regards,\nTalentLink Platform");

        return sb.toString();
    }

    @Transactional
    public CandidateResponse addCandidateToDemand(Long demandId, AddCandidateRequest request, User currentUser) {
        // 1. Find the demand and verify ownership
        Demand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new EntityNotFoundException("Demand not found with id: " + demandId));

        if (!demand.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to add candidates to this demand");
        }

        // 2. Find the vendor
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id: " + request.getVendorId()));

        // 3. Create and save the new candidate
        Candidate candidate = Candidate.builder()
                .name(request.getName())
                .resumeSummary(request.getResumeSummary())
                .demand(demand)
                .vendor(vendor)
                .status(CandidateStatus.SCREENING)
                .currentInterviewRound(1)
                // --- 设置新增字段 ---
                .gender(request.getGender())
                .skillset(request.getSkillset())
                .seniority(request.getSeniority())
                .relatedWorkingExperience(request.getRelatedWorkingExperience())
                .onboardingTime(request.getOnboardingTime())
                .skillHighlights(request.getSkillHighlights())
                .englishCapability(request.getEnglishCapability())
                .internalInterviewFeedback(request.getInternalInterviewFeedback())
                .onlineCodingResult(request.getOnlineCodingResult())
                .build();

        Candidate savedCandidate = candidateRepository.save(candidate);

        // 4. Log the creation event
        historyService.logAction(savedCandidate, currentUser, "CREATED", "Candidate added to demand '" + demand.getJobTitle() + "'");

        // 5. Return the response DTO
        return CandidateResponse.fromEntity(savedCandidate);
    }

    @Transactional(readOnly = true)
    public CandidateDetailResponse findCandidateByIdForOwner(Long candidateId, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));

        if (!candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to view this candidate");
        }

        return CandidateDetailResponse.fromEntity(candidate);
    }

    @Transactional
    public CandidateResponse updateCandidateStatus(Long candidateId, UpdateStatusRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));

        if (!candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to update this candidate's status");
        }

        String oldStatus = candidate.getStatus().name();
        candidate.setStatus(request.getStatus());
        Candidate updatedCandidate = candidateRepository.save(candidate);

        String details = "Status manually updated from " + oldStatus + " to " + updatedCandidate.getStatus().name();
        historyService.logAction(updatedCandidate, currentUser, "STATUS_MANUAL_UPDATE", details);

        return CandidateResponse.fromEntity(updatedCandidate);
    }
}
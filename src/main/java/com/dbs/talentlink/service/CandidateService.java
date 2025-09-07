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
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional
    public CandidateResponse updateOverrideRounds(Long candidateId, UpdateRoundsRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));

        // 权限校验
        if (!candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to update this candidate");
        }

        // 更新自定义轮次
        candidate.setTotalRoundsOverride(request.getTotalRounds());
        Candidate updatedCandidate = candidateRepository.save(candidate);

        // 记录历史
        String details = String.format("Total interview rounds for this candidate set to %d.", request.getTotalRounds());
        historyService.logAction(updatedCandidate, currentUser, "ROUNDS_OVERRIDE", details);

        return CandidateResponse.fromEntity(updatedCandidate);
    }

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
        // 1. 查找需求并验证权限 (逻辑不变)
        Demand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new EntityNotFoundException("Demand not found with id: " + demandId));

        if (!demand.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to add candidates to this demand");
        }

        // --- 核心修改点 ---
        // 2. 移除所有根据 vendorId 查找 Vendor 实体的旧逻辑
        //    我们现在直接接受并存储 request 中的 vendorName 字符串
        //
        //    Vendor vendor = vendorRepository.findById(request.getVendorId())  <-- REMOVE THIS
        //            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id: " + request.getVendorId())); <-- REMOVE THIS

        // 3. 构建 Candidate 实体，直接使用 vendorName
        Candidate candidate = Candidate.builder()
                .name(request.getName())
                .resumeSummary(request.getResumeSummary())
                .demand(demand)
                .vendorName(request.getVendorName()) // 直接使用 request 中的字符串
                .status(CandidateStatus.SCREENING)
                .currentInterviewRound(1)
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

        // 4. 记录历史和返回 DTO (逻辑不变)
        historyService.logAction(savedCandidate, currentUser, "CREATED", "Candidate added to demand '" + demand.getJobTitle() + "'");
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

    @Transactional(readOnly = true)
    public DuplicateCheckResponse checkDuplicate(CheckDuplicateRequest request) {
        // 1. 根据传入的 demandId 查找上下文需求
        Demand contextDemand = demandRepository.findById(request.getDemandId())
                .orElseThrow(() -> new EntityNotFoundException("Context demand not found with id: " + request.getDemandId()));

        Set<Specialty> contextSpecialties = contextDemand.getSpecialties();

        // 2. 如果上下文需求本身没有设置专业技能，则无法进行跨需求匹配，直接返回无重复
        if (contextSpecialties == null || contextSpecialties.isEmpty()) {
            return new DuplicateCheckResponse(false, List.of());
        }

        // 3. 使用新的 Repository 方法进行查询
        List<Candidate> matchingCandidates = candidateRepository
                .findDuplicatesInSimilarDemands(request.getName(), contextSpecialties);

        if (matchingCandidates.isEmpty()) {
            return new DuplicateCheckResponse(false, List.of());
        }

        List<CandidateResponse> responseDtos = matchingCandidates.stream()
                .map(CandidateResponse::fromEntity)
                .collect(Collectors.toList());

        return new DuplicateCheckResponse(true, responseDtos);
    }

    @Transactional
    public CandidateResponse updateCandidate(Long candidateId, UpdateCandidateRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));

        // 权限校验
        if (!candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to edit this candidate");
        }

        // --- 使用 Java 8 Optional 来优雅地处理部分更新 ---
        // 只有当请求中的字段不为 null 时，才进行更新
        java.util.Optional.ofNullable(request.getName()).ifPresent(candidate::setName);
        java.util.Optional.ofNullable(request.getVendorName()).ifPresent(candidate::setVendorName);
        java.util.Optional.ofNullable(request.getResumeSummary()).ifPresent(candidate::setResumeSummary);
        java.util.Optional.ofNullable(request.getGender()).ifPresent(candidate::setGender);
        java.util.Optional.ofNullable(request.getSkillset()).ifPresent(candidate::setSkillset);
        java.util.Optional.ofNullable(request.getSeniority()).ifPresent(candidate::setSeniority);
        java.util.Optional.ofNullable(request.getRelatedWorkingExperience()).ifPresent(candidate::setRelatedWorkingExperience);
        java.util.Optional.ofNullable(request.getOnboardingTime()).ifPresent(candidate::setOnboardingTime);
        java.util.Optional.ofNullable(request.getSkillHighlights()).ifPresent(candidate::setSkillHighlights);
        java.util.Optional.ofNullable(request.getEnglishCapability()).ifPresent(candidate::setEnglishCapability);
        java.util.Optional.ofNullable(request.getInternalInterviewFeedback()).ifPresent(candidate::setInternalInterviewFeedback);
        java.util.Optional.ofNullable(request.getOnlineCodingResult()).ifPresent(candidate::setOnlineCodingResult);

        Candidate updatedCandidate = candidateRepository.save(candidate);

        // 记录历史
        historyService.logAction(updatedCandidate, currentUser, "CANDIDATE_UPDATE", "Candidate details updated.");

        return CandidateResponse.fromEntity(updatedCandidate);
    }
}
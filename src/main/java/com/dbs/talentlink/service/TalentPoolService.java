package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.CandidateResponse;
import com.dbs.talentlink.dto.ReactivateCandidateRequest;
import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.CandidateStatus;
import com.dbs.talentlink.repository.CandidateRepository;
import com.dbs.talentlink.repository.DemandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TalentPoolService {
    private final CandidateRepository candidateRepository;
    private final DemandRepository demandRepository;
    private final CandidateHistoryService historyService;

    @Transactional(readOnly = true)
    public Page<CandidateResponse> findTalentPool(String name, String specialty, Pageable pageable) {
        // 使用 Specification 构建动态查询
        Specification<Candidate> spec = Specification.where((root, query, cb) -> cb.equal(root.get("status"), CandidateStatus.ON_HOLD));

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(specialty)) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("skillset")), "%" + specialty.toLowerCase() + "%"));
        }

        return candidateRepository.findAll(spec, pageable).map(CandidateResponse::fromEntity);
    }

    @Transactional
    public CandidateResponse reactivateCandidate(ReactivateCandidateRequest request, User currentUser) {
        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));

        Demand targetDemand = demandRepository.findById(request.getTargetDemandId())
                .orElseThrow(() -> new EntityNotFoundException("Target Demand not found"));

        // 权限校验：确保操作的 HM 是目标 Demand 的创建者
        if (!targetDemand.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to reactivate candidates for this demand");
        }

        // 业务校验：确保候选人处于 ON_HOLD 状态
        if (candidate.getStatus() != CandidateStatus.ON_HOLD) {
            throw new IllegalStateException("Only candidates with ON_HOLD status can be reactivated.");
        }

        // 执行激活操作
        candidate.setDemand(targetDemand);
        candidate.setStatus(CandidateStatus.SCREENING);
        candidate.setCurrentInterviewRound(1);
        candidate.setTotalRoundsOverride(null); // 清空自定义轮数

        Candidate reactivatedCandidate = candidateRepository.save(candidate);

        // 记录历史
        String details = String.format("Reactivated from Talent Pool and added to demand '%s'.", targetDemand.getJobTitle());
        historyService.logAction(reactivatedCandidate, currentUser, "REACTIVATED", details);

        return CandidateResponse.fromEntity(reactivatedCandidate);
    }
}
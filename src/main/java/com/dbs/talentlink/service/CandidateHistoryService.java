package com.dbs.talentlink.service;

import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.entity.CandidateHistory;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.CandidateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.dbs.talentlink.dto.HistoryResponse;
import com.dbs.talentlink.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateHistoryService {

    private final CandidateHistoryRepository historyRepository;
    private final CandidateRepository candidateRepository;

    public void logAction(Candidate candidate, User user, String actionType, String details) {
        CandidateHistory historyLog = CandidateHistory.builder()
                .candidate(candidate)
                .createdBy(user)
                .actionType(actionType)
                .details(details)
                .build();
        historyRepository.save(historyLog);
    }

    @Transactional(readOnly = true)
    public Page<HistoryResponse> findHistoryForCandidate(Long candidateId, User currentUser, Pageable pageable) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));

        // --- 新增的复杂权限校验逻辑 ---
        boolean hasPermission = false;
        if (currentUser.getRole() == Role.HM) {
            // 如果是 HM，必须是需求的创建者
            if (candidate.getDemand().getCreatedBy().getId().equals(currentUser.getId())) {
                hasPermission = true;
            }
        } else if (currentUser.getRole() == Role.INTERVIEWER) {
            // 如果是 Interviewer，专业技能必须匹配
            boolean isFullstack = currentUser.getSpecialties().stream()
                    .anyMatch(s -> "FULLSTACK".equalsIgnoreCase(s.getName()));
            if (isFullstack || !Collections.disjoint(currentUser.getSpecialties(), candidate.getDemand().getSpecialties())) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new AccessDeniedException("User does not have permission to view this candidate's history");
        }

        return historyRepository.findByCandidateId(candidateId, pageable)
                .map(HistoryResponse::fromEntity);
    }
}
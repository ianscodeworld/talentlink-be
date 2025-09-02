package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.CreateDemandRequest;
import com.dbs.talentlink.dto.DemandDetailResponse;
import com.dbs.talentlink.dto.DemandResponse;
import com.dbs.talentlink.dto.UpdateDemandStatusRequest;
import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.entity.Specialty;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.DemandStatus;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.DemandRepository;
import com.dbs.talentlink.repository.SpecialtyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandService {

    private final DemandRepository demandRepository;
    private final SpecialtyRepository specialtyRepository;


    @Transactional
    public DemandResponse createDemand(CreateDemandRequest request, User currentUser) {
        Set<Specialty> specialties = null;
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            specialties = specialtyRepository.findByNameIn(request.getSpecialties());
        }

        Demand demand = Demand.builder()
                .jobTitle(request.getJobTitle())
                .description(request.getDescription())
                .totalInterviewRounds(request.getTotalInterviewRounds())
                .status(DemandStatus.OPEN)
                .createdBy(currentUser)
                .specialties(specialties) // 设置专业技能
                .build();

        Demand savedDemand = demandRepository.save(demand);
        return DemandResponse.fromEntity(savedDemand);
    }

    @Transactional(readOnly = true)
    public Page<DemandResponse> findDemandsForCurrentUser(User currentUser, Pageable pageable) {
        Page<Demand> demandsPage = demandRepository.findByCreatedById(currentUser.getId(), pageable);
        return demandsPage.map(DemandResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public DemandDetailResponse findDemandById(Long demandId, User currentUser) {
        Demand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new EntityNotFoundException("Demand not found with id: " + demandId));

        // --- 新增的、更复杂的权限校验逻辑 ---
        boolean hasPermission = false;
        if (currentUser.getRole() == Role.HM) {
            // 1. 如果是 HM, 检查是否为创建者
            if (demand.getCreatedBy().getId().equals(currentUser.getId())) {
                hasPermission = true;
            }
        } else if (currentUser.getRole() == Role.INTERVIEWER) {
            // 2. 如果是 Interviewer, 检查专业技能是否匹配
            boolean isFullstack = currentUser.getSpecialties().stream()
                    .anyMatch(s -> "FULLSTACK".equalsIgnoreCase(s.getName()));

            if (isFullstack || !Collections.disjoint(currentUser.getSpecialties(), demand.getSpecialties())) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new AccessDeniedException("User does not have permission to view this demand");
        }
        // --- 权限校验结束 ---

        return DemandDetailResponse.fromEntity(demand);
    }

    @Transactional
    public DemandResponse updateDemandStatus(Long demandId, UpdateDemandStatusRequest request, User currentUser) {
        // 1. 查找需求
        Demand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new EntityNotFoundException("Demand not found with id: " + demandId));

        // 2. 验证操作权限，只有创建者才能修改
        if (!demand.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to update this demand's status");
        }

        // 3. 更新状态并保存
        demand.setStatus(request.getStatus());
        Demand updatedDemand = demandRepository.save(demand);

        // 4. 返回更新后的数据
        return DemandResponse.fromEntity(updatedDemand);
    }

    @Transactional(readOnly = true)
    public Page<DemandResponse> findRelevantDemands(User currentUser, Pageable pageable) {
        Set<Specialty> userSpecialties = currentUser.getSpecialties();

        // 如果用户没有设置任何专业技能，则返回空列表
        if (userSpecialties == null || userSpecialties.isEmpty()) {
            return Page.empty(pageable);
        }

        // 检查用户是否拥有 "FULLSTACK" 技能
        boolean isFullstack = userSpecialties.stream()
                .anyMatch(s -> "FULLSTACK".equalsIgnoreCase(s.getName()));

        Page<Demand> demandsPage;
        if (isFullstack) {
            // 如果是 FULLSTACK，则返回所有 OPEN 状态的需求
            demandsPage = demandRepository.findByStatus(DemandStatus.OPEN, pageable);
        } else {
            // 否则，只返回专业技能匹配的 OPEN 状态的需求
            demandsPage = demandRepository.findByStatusAndSpecialtiesIn(DemandStatus.OPEN, userSpecialties, pageable);
        }

        return demandsPage.map(DemandResponse::fromEntity);
    }
}
package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.*;
import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.entity.Squad;
import com.dbs.talentlink.model.SquadStatus;
import com.dbs.talentlink.repository.SquadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final SquadRepository squadRepository;

    @Transactional
    public SquadResponse createSquad(CreateSquadRequest request) {
        Squad squad = new Squad();
        squad.setName(request.getName());
        Squad savedSquad = squadRepository.save(squad);
        return SquadResponse.fromEntity(savedSquad);
    }

    @Transactional(readOnly = true)
    public Page<SquadResponse> findAll(Pageable pageable) {
        return squadRepository.findAll(pageable).map(SquadResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public SquadDetailResponse findById(Long squadId) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new EntityNotFoundException("Squad not found with id: " + squadId));

        // --- 将 Set 转换为 List ---
        List<DemandResponse> demandDtos = new ArrayList<>(squad.getDemands()).stream()
                .map(DemandResponse::fromEntity)
                .collect(Collectors.toList());

        // 聚合计算逻辑
        Map<String, Long> statusCounts = squad.getDemands().stream()
                .flatMap(demand -> demand.getCandidates().stream())
                .collect(Collectors.groupingBy(
                        candidate -> candidate.getStatus().name(),
                        Collectors.counting()
                ));

        return SquadDetailResponse.fromEntity(squad, statusCounts, demandDtos);
    }

    @Transactional
    public SquadResponse updateStatus(Long squadId, UpdateSquadStatusRequest request) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new EntityNotFoundException("Squad not found with id: " + squadId));
        squad.setStatus(request.getStatus());
        Squad updatedSquad = squadRepository.save(squad);
        return SquadResponse.fromEntity(updatedSquad);
    }

    @Transactional
    public void softDelete(Long squadId) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new EntityNotFoundException("Squad not found with id: " + squadId));
        squad.setDeleted(true);
        squadRepository.save(squad);
    }

    // 用新逻辑替换旧的 findAll 方法
    @Transactional(readOnly = true)
    public Page<SquadResponse> findAll(SquadStatus status, Pageable pageable) {
        Page<Squad> squadsPage;
        if (status != null) {
            // 如果提供了 status 参数，则按状态查询
            squadsPage = squadRepository.findByStatusAndIsDeletedFalse(status, pageable);
        } else {
            // 否则，默认只返回 ACTIVE 状态的
            squadsPage = squadRepository.findByStatusAndIsDeletedFalse(SquadStatus.ACTIVE, pageable);
        }
        return squadsPage.map(SquadResponse::fromEntity);
    }
}
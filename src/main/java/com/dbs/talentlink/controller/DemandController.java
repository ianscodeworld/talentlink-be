package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.*;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.CandidateService;
import com.dbs.talentlink.service.DemandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demands")
@RequiredArgsConstructor
public class DemandController {

    private final DemandService demandService;
    private final CandidateService candidateService;

    @PostMapping
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<DemandResponse> createDemand(
            @Valid @RequestBody CreateDemandRequest request,
            @AuthenticationPrincipal User currentUser) {
        DemandResponse createdDemand = demandService.createDemand(request, currentUser);
        return new ResponseEntity<>(createdDemand, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DemandResponse>> getMyDemands(
            @AuthenticationPrincipal User currentUser, Pageable pageable) { // 直接注入 Pageable
        Page<DemandResponse> demands = demandService.findDemandsForCurrentUser(currentUser, pageable);
        return ResponseEntity.ok(demands);
    }

    @PostMapping("/{demandId}/candidates")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<CandidateResponse> addCandidate(
            @PathVariable Long demandId,
            @Valid @RequestBody AddCandidateRequest request,
            @AuthenticationPrincipal User currentUser) {
        CandidateResponse createdCandidate = candidateService.addCandidateToDemand(demandId, request, currentUser);
        return new ResponseEntity<>(createdCandidate, HttpStatus.CREATED);
    }

    @GetMapping("/{demandId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DemandDetailResponse> getDemandById(
            @PathVariable Long demandId,
            @AuthenticationPrincipal User currentUser) {
        DemandDetailResponse demandDetail = demandService.findDemandById(demandId, currentUser);
        return ResponseEntity.ok(demandDetail);
    }

    @PutMapping("/{demandId}/status")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<DemandResponse> updateDemandStatus(
            @PathVariable Long demandId,
            @Valid @RequestBody UpdateDemandStatusRequest request,
            @AuthenticationPrincipal User currentUser) {
        DemandResponse updatedDemand = demandService.updateDemandStatus(demandId, request, currentUser);
        return ResponseEntity.ok(updatedDemand);
    }

    @GetMapping("/relevant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DemandResponse>> getRelevantDemands(
            @AuthenticationPrincipal User currentUser, Pageable pageable) {
        Page<DemandResponse> demands = demandService.findRelevantDemands(currentUser, pageable);
        return ResponseEntity.ok(demands);
    }


}
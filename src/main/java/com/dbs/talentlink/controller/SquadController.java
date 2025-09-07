package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.CreateSquadRequest;
import com.dbs.talentlink.dto.SquadDetailResponse;
import com.dbs.talentlink.dto.SquadResponse;
import com.dbs.talentlink.dto.UpdateSquadStatusRequest;
import com.dbs.talentlink.model.SquadStatus;
import com.dbs.talentlink.service.SquadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/squads")
@RequiredArgsConstructor
public class SquadController {

    private final SquadService squadService;

    @PostMapping
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<SquadResponse> createSquad(@Valid @RequestBody CreateSquadRequest request) {
        SquadResponse squad = squadService.createSquad(request);
        return new ResponseEntity<>(squad, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SquadResponse>> getAllSquads(
            @RequestParam(required = false) SquadStatus status, // 增加可选参数
            Pageable pageable) {
        Page<SquadResponse> squads = squadService.findAll(status, pageable);
        return ResponseEntity.ok(squads);
    }

    @GetMapping("/{squadId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SquadDetailResponse> getSquadById(@PathVariable Long squadId) {
        SquadDetailResponse squadDetail = squadService.findById(squadId);
        return ResponseEntity.ok(squadDetail);
    }

    @PutMapping("/{squadId}/status")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<SquadResponse> updateSquadStatus(
            @PathVariable Long squadId,
            @Valid @RequestBody UpdateSquadStatusRequest request) {
        SquadResponse updatedSquad = squadService.updateStatus(squadId, request);
        return ResponseEntity.ok(updatedSquad);
    }

    @DeleteMapping("/{squadId}")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<Void> deleteSquad(@PathVariable Long squadId) {
        squadService.softDelete(squadId);
        return ResponseEntity.noContent().build();
    }


}
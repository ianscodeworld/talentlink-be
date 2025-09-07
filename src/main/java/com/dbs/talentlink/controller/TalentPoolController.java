// com/dbs/talentlink/controller/TalentPoolController.java
package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.CandidateResponse;
import com.dbs.talentlink.dto.ReactivateCandidateRequest;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.TalentPoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/talent-pool")
@RequiredArgsConstructor
public class TalentPoolController {

    private final TalentPoolService talentPoolService;

    @GetMapping
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<Page<CandidateResponse>> getTalentPool(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            Pageable pageable) {
        Page<CandidateResponse> talentPool = talentPoolService.findTalentPool(name, specialty, pageable);
        return ResponseEntity.ok(talentPool);
    }

    @PostMapping("/reactivate")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<CandidateResponse> reactivateCandidate(
            @Valid @RequestBody ReactivateCandidateRequest request,
            @AuthenticationPrincipal User currentUser) {
        CandidateResponse reactivatedCandidate = talentPoolService.reactivateCandidate(request, currentUser);
        return ResponseEntity.ok(reactivatedCandidate);
    }
}
package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.CandidateResponse;
import com.dbs.talentlink.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/candidates")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<Page<CandidateResponse>> searchCandidates(
            @RequestParam("q") String query,
            Pageable pageable) {
        Page<CandidateResponse> results = searchService.searchCandidates(query, pageable);
        return ResponseEntity.ok(results);
    }
}
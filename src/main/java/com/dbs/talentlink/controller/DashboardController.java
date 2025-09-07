// com/dbs/talentlink/controller/DashboardController.java
package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.HmDashboardResponse;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/hm-summary")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<HmDashboardResponse> getHmSummary(@AuthenticationPrincipal User currentUser) {
        HmDashboardResponse summary = dashboardService.getHmDashboardSummary(currentUser);
        return ResponseEntity.ok(summary);
    }
}
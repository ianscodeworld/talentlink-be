package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.UpdatePasswordRequest;
import com.dbs.talentlink.dto.UpdateSpecialtyRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(@RequestParam("role") Role role, Pageable pageable) {
        Page<UserResponse> users = userService.findByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me/specialty")
    @PreAuthorize("isAuthenticated()") // 任何登录用户都可以设置自己的技能
    public ResponseEntity<UserResponse> updateMySpecialty(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateSpecialtyRequest request) {
        UserResponse updatedUser = userService.updateMySpecialty(currentUser, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateMyPassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updateMyPassword(currentUser, request);
        return ResponseEntity.ok().build();
    }
}
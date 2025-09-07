package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.CreateUserRequest;
import com.dbs.talentlink.dto.UpdateUserRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse newUser = adminService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build(); // 204 No Content 是 DELETE 成功的标准响应
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('HM')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) { // 无需 @Valid, 因为字段都是可选的
        UserResponse updatedUser = adminService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
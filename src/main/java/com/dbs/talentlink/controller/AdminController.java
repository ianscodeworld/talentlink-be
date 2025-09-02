package com.dbs.talentlink.controller;

import com.dbs.talentlink.dto.CreateUserRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
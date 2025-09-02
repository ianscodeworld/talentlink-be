package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.CreateUserRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.entity.Specialty;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.SpecialtyRepository;
import com.dbs.talentlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    // 定义一个默认的临时密码
    private static final String DEFAULT_TEMP_PASSWORD = "Welcome123!";

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 1. 检查邮箱是否已被使用
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email already in use: " + request.getEmail());
        });

        // 2. 根据名称查找并设置专业技能
        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            specialties = specialtyRepository.findByNameIn(request.getSpecialties());
        }

        // 3. 构建新的用户实体
        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // HM 默认创建 INTERVIEWER 角色
                .role(Role.INTERVIEWER)
                // 使用默认密码并进行哈希加密
                .password(passwordEncoder.encode(DEFAULT_TEMP_PASSWORD))
                // 强制新用户首次登录时修改密码
                .passwordChangeRequired(true)
                .specialties(specialties)
                .build();

        // 4. 保存用户并返回 DTO
        User savedUser = userRepository.save(newUser);
        return UserResponse.fromEntity(savedUser);
    }
}
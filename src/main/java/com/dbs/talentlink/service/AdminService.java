package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.CreateUserRequest;
import com.dbs.talentlink.dto.UpdateUserRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.entity.Specialty;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.SpecialtyRepository;
import com.dbs.talentlink.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
        // --- 核心修改点 ---
        // 增加一个校验，确保只能创建 HM 或 INTERVIEWER
        if (request.getRole() != Role.HM && request.getRole() != Role.INTERVIEWER) {
            throw new IllegalArgumentException("Invalid role specified. Can only create HM or INTERVIEWER.");
        }

        // 3. 构建新的用户实体
        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // 从请求中动态获取角色
                .role(request.getRole())
                .password(passwordEncoder.encode(DEFAULT_TEMP_PASSWORD))
                .passwordChangeRequired(true)
                .specialties(specialties)
                // isActive 默认为 true (来自 User 实体的 @Builder.Default)
                .isActive(true)
                .build();

        // 4. 保存用户并返回 DTO
        User savedUser = userRepository.save(newUser);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 执行软删除
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 更新角色 (如果请求中提供了)
        if (request.getRole() != null) {
            // 增加校验，确保只能设置为 HM 或 INTERVIEWER
            if (request.getRole() != Role.HM && request.getRole() != Role.INTERVIEWER) {
                throw new IllegalArgumentException("Invalid role specified. Can only set to HM or INTERVIEWER.");
            }
            user.setRole(request.getRole());
        }

        // 更新专业技能 (如果请求中提供了)
        if (request.getSpecialties() != null) {
            Set<Specialty> newSpecialties = specialtyRepository.findByNameIn(request.getSpecialties());
            user.setSpecialties(newSpecialties);
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }
}
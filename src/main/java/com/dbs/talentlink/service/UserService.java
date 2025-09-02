package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.UpdateSpecialtyRequest;
import com.dbs.talentlink.dto.UserResponse;
import com.dbs.talentlink.entity.Specialty;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.Role;
import com.dbs.talentlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dbs.talentlink.repository.SpecialtyRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.dbs.talentlink.dto.UpdatePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> findByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(UserResponse::fromEntity);
    }

    @Transactional
    public UserResponse updateMySpecialty(User currentUser, UpdateSpecialtyRequest request) {
        Set<Specialty> newSpecialties = specialtyRepository.findByNameIn(request.getSpecialties());
        currentUser.setSpecialties(newSpecialties);
        User updatedUser = userRepository.save(currentUser);
        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void updateMyPassword(User currentUser, UpdatePasswordRequest request) {
        // 在真实项目中，这里应增加更多密码策略校验 (例如，不能与旧密码相同)

        // 1. 哈希新密码
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // 2. 解除强制修改密码的标志
        currentUser.setPasswordChangeRequired(false);
        // 3. 保存更新后的用户
        userRepository.save(currentUser);
    }
}
package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    // --- 新增字段 ---
    @NotNull(message = "Role cannot be null")
    private Role role;
    // 专长列表，可以为空
    private List<String> specialties;
}
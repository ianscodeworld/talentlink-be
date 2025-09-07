package com.dbs.talentlink.dto;

import com.dbs.talentlink.model.Role;
import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRequest {
    // 两个字段都是可选的，允许只更新其中一个
    private Role role;
    private List<String> specialties;
}
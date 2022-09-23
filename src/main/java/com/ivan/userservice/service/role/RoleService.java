package com.ivan.userservice.service.role;

import com.ivan.userservice.domain.Role;

import java.util.List;

public interface RoleService {

    Role save(Role role);

    List<Role> findAll();

}

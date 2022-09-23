package com.ivan.userservice.repo.role;

import com.ivan.userservice.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepo {

    Optional<Role> findByName(String name);

    Role save(Role role);

    List<Role> findAll();

}

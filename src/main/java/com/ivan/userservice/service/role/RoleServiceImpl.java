package com.ivan.userservice.service.role;

import com.ivan.userservice.domain.Role;
import com.ivan.userservice.repo.role.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    public Role save(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepo.findAll();
    }

}

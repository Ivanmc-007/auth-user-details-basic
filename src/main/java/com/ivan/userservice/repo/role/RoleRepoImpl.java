package com.ivan.userservice.repo.role;

import com.ivan.userservice.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RoleRepoImpl implements RoleRepo {

    private long idRole = 1L;

    private final Map<Long, Role> roleDB = new HashMap<Long, Role>() {
//        {
//            this.put(1L, Role.builder()
//                    .id(1L)
//                    .name("USER")
//                    .build());
//        }
    };

    @Override
    public Optional<Role> findByName(String name) {
        for (Map.Entry<Long, Role> entry : roleDB.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Role save(Role role) {
        long currId = idRole;
        role.setId(currId);
        roleDB.put(currId, role);
        idRole++;
        return role;
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(roleDB.values());
    }
}

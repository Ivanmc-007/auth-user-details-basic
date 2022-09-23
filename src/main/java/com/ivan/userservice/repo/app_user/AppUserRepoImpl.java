package com.ivan.userservice.repo.app_user;

import com.ivan.userservice.domain.AppUser;
import com.ivan.userservice.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AppUserRepoImpl implements AppUserRepo {

    private final Map<Long, AppUser> userDB = new HashMap<Long, AppUser>() {
        {
            this.put(1L, AppUser.builder()
                    .id(1L)
                    .name("Will Smith")
                    .username("will_smith")
                    .password("1234")
                    .roles(Collections.singleton(Role.builder()
                            .id(1L)
                            .name("ROLE_USER")
                            .build()))
                    .build());
        }
    };

    @Override
    public Optional<AppUser> findById(Long userId) {
        return Optional.ofNullable(userDB.get(userId));
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        for (Map.Entry<Long, AppUser> entry : userDB.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByName(String name) {
        for (Map.Entry<Long, AppUser> entry : userDB.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AppUser> findAll() {
        return new ArrayList<>(userDB.values());
    }
}

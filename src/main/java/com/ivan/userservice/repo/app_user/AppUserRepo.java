package com.ivan.userservice.repo.app_user;


import com.ivan.userservice.domain.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserRepo {

    Optional<AppUser> findById(Long userId);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByName(String name);

    List<AppUser> findAll();

}

package com.ivan.userservice.service.app_user;

import com.ivan.userservice.domain.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserService {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAppUsers();

}

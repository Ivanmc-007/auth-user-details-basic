package com.ivan.userservice.service.app_user;

import com.ivan.userservice.domain.AppUser;
import com.ivan.userservice.repo.app_user.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepo appUserRepo;

    /**
     * Load user by username
     * NOTE: required method for checking USER by login and password
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepo.findByUsername(username).orElse(null);
        if (Objects.isNull(appUser)) {
            log.error("User not found in DB");
            throw new UsernameNotFoundException("User not found in DB");
        } else {
            // TODO: remove info or change logging level
            log.info("User found in DB: {}", username);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            appUser.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            return new User(appUser.getUsername(), appUser.getPassword(), authorities);
        }
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        log.info("Fetching appUser {}", username);
        return appUserRepo.findByUsername(username);
    }

    @Override
    public List<AppUser> findAppUsers() {
        return appUserRepo.findAll();
    }

}

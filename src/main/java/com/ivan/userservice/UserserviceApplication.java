package com.ivan.userservice;

import com.ivan.userservice.domain.Role;
import com.ivan.userservice.service.role.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(RoleService roleService) {
        return args -> {
            roleService.save(new Role(null, "ROLE_USER"));
            roleService.save(new Role(null, "ROLE_MANAGER"));
            roleService.save(new Role(null, "ROLE_ADMIN"));
            roleService.save(new Role(null, "ROLE_SUPER_ADMIN"));
        };
    }

}

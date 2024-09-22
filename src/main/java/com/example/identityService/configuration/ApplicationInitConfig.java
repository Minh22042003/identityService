package com.example.identityService.configuration;

import com.example.identityService.entity.Permission;
import com.example.identityService.entity.Role;
import com.example.identityService.entity.User;
import com.example.identityService.repository.RoleRepository;
import com.example.identityService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUserName("admin").isEmpty()) {
//                var role = new HashSet<String>();
//                role.add(Role.Admin.name());
                var role = roleRepository.findById("Admin").orElseGet(() -> {
                    Role newRole = new Role("Admin", "Admin role", new HashSet<Permission>());
                    return roleRepository.save(newRole);
                });
                var roles = new HashSet<Role>();
                roles.add(role);
                User user = User.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user created with default password:admin");
            }
        };
    }
}

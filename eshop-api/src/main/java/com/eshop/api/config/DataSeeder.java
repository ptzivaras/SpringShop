package com.eshop.api.config;

import com.eshop.api.domain.Role;
import com.eshop.api.domain.User;
import com.eshop.api.repository.RoleRepository;
import com.eshop.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        Role roleUser = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_USER").build()));
        Role roleAdmin = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_ADMIN").build()));

        userRepo.findByUsername("admin").orElseGet(() -> {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@eshop.local")
                    .password(encoder.encode("Admin123!"))  // change after first login
                    .roles(Set.of(roleAdmin, roleUser))
                    .build();
            return userRepo.save(admin);
        });
    }
}

package com.eshop.api.api;

import com.eshop.api.domain.Role;
import com.eshop.api.domain.User;
import com.eshop.api.dto.*;
import com.eshop.api.repository.RoleRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.util.JwtUtil;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepo,
                          RoleRepository roleRepo,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Validated @RequestBody RegisterRequest req) {
        // Check for existing username/email
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Assign ROLE_USER by default
        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(
                        Role.builder().name("ROLE_USER").build()
                ));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepo.save(user);

        // Auto-login after registration
        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.builder()
                .accessToken(token)
                .build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Validated @RequestBody LoginRequest req) {
        // Authenticate credentials
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsernameOrEmail(),
                        req.getPassword()
                )
        );

        // Generate JWT
        String username = auth.getName();
        String token = jwtUtil.generateToken(username);

        return AuthResponse.builder()
                .accessToken(token)
                .build();
    }
}

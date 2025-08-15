package com.eshop.api.service;

import com.eshop.api.dto.AuthResponse;
import com.eshop.api.dto.LoginRequest;
import com.eshop.api.dto.RegisterRequest;
import com.eshop.api.domain.Role;
import com.eshop.api.domain.User;
import com.eshop.api.repository.RoleRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Missing role USER"));
        u.getRoles().add(userRole);

        userRepository.save(u);

        String token = jwtUtil.generateToken(u.getUsername());
        return new AuthResponse(token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new IllegalStateException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(u.getUsername());
        return new AuthResponse(token);
    }
}

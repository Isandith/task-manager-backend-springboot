package com.taskmanager.task_manager.service.impl;

import com.taskmanager.task_manager.dto.request.LoginRequest;
import com.taskmanager.task_manager.dto.request.RegisterRequest;
import com.taskmanager.task_manager.dto.response.AuthResponse;
import com.taskmanager.task_manager.entity.User;
import com.taskmanager.task_manager.enums.Role;
import com.taskmanager.task_manager.exception.BadRequestException;
import com.taskmanager.task_manager.repository.UserRepository;
import com.taskmanager.task_manager.security.JwtService;
import com.taskmanager.task_manager.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication service implementation for register and login flows.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a standard user and returns an authentication response.
     *
     * @param request registration payload
     * @return generated token and user metadata
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        if (request.getRole() != null && request.getRole().trim().length() > 0
                && !"USER".equalsIgnoreCase(request.getRole().trim())) {
            throw new BadRequestException("Self-registration can only create USER accounts");
        }
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getUsername())
                .password(savedUser.getPassword())
                .authorities("ROLE_" + savedUser.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", savedUser.getUsername(), savedUser.getRole().name());
    }

    /**
     * Registers an administrator account and returns an authentication response.
     *
     * @param request registration payload
     * @return generated token and user metadata
     */
    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getUsername())
                .password(savedUser.getPassword())
                .authorities("ROLE_" + savedUser.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", savedUser.getUsername(), savedUser.getRole().name());
    }

    /**
     * Authenticates a user and returns an authentication response.
     *
     * @param request login credentials
     * @return generated token and user metadata
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        User user = optionalUser.get();

        return new AuthResponse(token, "Bearer", user.getUsername(), user.getRole().name());
    }
}

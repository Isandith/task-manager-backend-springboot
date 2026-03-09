package com.taskmanager.task_manager.controller;

import com.taskmanager.task_manager.dto.request.LoginRequest;
import com.taskmanager.task_manager.dto.request.RegisterRequest;
import com.taskmanager.task_manager.dto.response.AuthResponse;
import com.taskmanager.task_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for registration and login.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a standard user account.
     *
     * @param request registration payload
     * @return authentication response with token
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a standard USER account and returns a JWT authentication response."
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<AuthResponse>(response, HttpStatus.CREATED);
    }

    /**
     * Registers a new administrator account.
     *
     * @param request registration payload
     * @return authentication response with token
     */
    @PostMapping("/register-admin")
    @Operation(
            summary = "Register a new ADMIN user",
            description = "Creates an ADMIN account and returns a JWT authentication response. This endpoint requires ADMIN authorization.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return new ResponseEntity<AuthResponse>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and returns an access token.
     *
     * @param request login payload
     * @return authentication response with token
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login with credentials",
            description = "Authenticates a user using username and password, then returns a JWT authentication response."
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return new ResponseEntity<AuthResponse>(response, HttpStatus.OK);
    }
}

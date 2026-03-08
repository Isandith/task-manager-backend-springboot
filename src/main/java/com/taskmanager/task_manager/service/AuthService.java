package com.taskmanager.task_manager.service;

import com.taskmanager.task_manager.dto.request.LoginRequest;
import com.taskmanager.task_manager.dto.request.RegisterRequest;
import com.taskmanager.task_manager.dto.response.AuthResponse;

/**
 * Contract for authentication and registration operations.
 */
public interface AuthService {

    /**
     * Registers a standard user account.
     *
     * @param request registration payload
     * @return authentication response containing token and user metadata
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Registers an administrator account.
     *
     * @param request registration payload
     * @return authentication response containing token and user metadata
     */
    AuthResponse registerAdmin(RegisterRequest request);

    /**
     * Authenticates a user with credentials.
     *
     * @param request login payload
     * @return authentication response containing token and user metadata
     */
    AuthResponse login(LoginRequest request);
}

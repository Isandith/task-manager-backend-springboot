package com.taskmanager.task_manager.service;

import com.taskmanager.task_manager.dto.request.LoginRequest;
import com.taskmanager.task_manager.dto.request.RegisterRequest;
import com.taskmanager.task_manager.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse registerAdmin(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

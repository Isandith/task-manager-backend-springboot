package com.taskmanager.task_manager.service.impl;

import com.taskmanager.task_manager.dto.request.RegisterRequest;
import com.taskmanager.task_manager.dto.response.AuthResponse;
import com.taskmanager.task_manager.entity.User;
import com.taskmanager.task_manager.enums.Role;
import com.taskmanager.task_manager.exception.BadRequestException;
import com.taskmanager.task_manager.repository.UserRepository;
import com.taskmanager.task_manager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_WithAdminRole_ThrowsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin_try");
        request.setEmail("admin@demo.com");
        request.setPassword("secret123");
        request.setRole("ADMIN");

        when(userRepository.existsByUsername("admin_try")).thenReturn(false);
        when(userRepository.existsByEmail("admin@demo.com")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithoutRole_CreatesUserRoleAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@demo.com");
        request.setPassword("secret123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed_password");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("john");
        savedUser.setEmail("john@demo.com");
        savedUser.setPassword("hashed_password");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertEquals(Role.USER, captor.getValue().getRole());
        assertEquals("jwt-token", response.getToken());
        assertEquals("USER", response.getRole());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void registerAdmin_CreatesAdminRoleAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("root_admin");
        request.setEmail("root@demo.com");
        request.setPassword("secret123");

        when(userRepository.existsByUsername("root_admin")).thenReturn(false);
        when(userRepository.existsByEmail("root@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed_password");

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setUsername("root_admin");
        savedUser.setEmail("root@demo.com");
        savedUser.setPassword("hashed_password");
        savedUser.setRole(Role.ADMIN);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-admin-token");

        AuthResponse response = authService.registerAdmin(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertEquals(Role.ADMIN, captor.getValue().getRole());
        assertEquals("jwt-admin-token", response.getToken());
        assertEquals("ADMIN", response.getRole());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void registerAdmin_WithDuplicateUsername_ThrowsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing_admin");
        request.setEmail("existing@demo.com");
        request.setPassword("secret123");

        when(userRepository.existsByUsername("existing_admin")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registerAdmin(request));

        verify(userRepository, never()).save(any(User.class));
    }
}

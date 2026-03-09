package com.taskmanager.task_manager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.task_manager.dto.response.UserResponse;
import com.taskmanager.task_manager.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * REST endpoints for user administration operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns all users. Accessible only by ADMIN role.
     *
     * @return list of users
     */
    @GetMapping
    @Operation(
            summary = "List all users",
            description = "Returns all users in the system. This endpoint requires ADMIN authorization."
    )
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

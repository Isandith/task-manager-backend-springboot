package com.taskmanager.task_manager.service;

import java.util.List;

import com.taskmanager.task_manager.dto.response.UserResponse;

/**
 * Contract for user-related read operations.
 */
public interface UserService {

    /**
     * Returns all users in the system.
     *
     * @return list of users
     */
    List<UserResponse> getAllUsers();
}

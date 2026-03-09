package com.taskmanager.task_manager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taskmanager.task_manager.dto.response.UserResponse;
import com.taskmanager.task_manager.entity.User;
import com.taskmanager.task_manager.repository.UserRepository;
import com.taskmanager.task_manager.service.UserService;

/**
 * User service implementation for administrative user queries.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responseList = new ArrayList<>();

        int i;
        for (i = 0; i < users.size(); i++) {
            responseList.add(mapToResponse(users.get(i)));
        }

        return responseList;
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            response.setRole(user.getRole().name());
        }

        return response;
    }
}

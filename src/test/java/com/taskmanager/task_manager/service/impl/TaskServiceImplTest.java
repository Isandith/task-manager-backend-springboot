package com.taskmanager.task_manager.service.impl;

import com.taskmanager.task_manager.dto.response.TaskResponse;
import com.taskmanager.task_manager.entity.Task;
import com.taskmanager.task_manager.entity.User;
import com.taskmanager.task_manager.enums.Role;
import com.taskmanager.task_manager.enums.TaskPriority;
import com.taskmanager.task_manager.enums.TaskStatus;
import com.taskmanager.task_manager.exception.UnauthorizedActionException;
import com.taskmanager.task_manager.repository.TaskRepository;
import com.taskmanager.task_manager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for task access control and retrieval logic.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    /**
     * Clears authentication context after each test to prevent leakage.
     */
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Verifies USER role can only retrieve user-scoped task pages.
     */
    @Test
    void getTasks_WithUserRole_ReturnsOnlyUserScopedTasks() {
        setAuth("john");

        User currentUser = new User();
        currentUser.setId(10);
        currentUser.setUsername("john");
        currentUser.setRole(Role.USER);

        Task task = buildTask(100, currentUser);
        Page<Task> taskPage = new PageImpl<Task>(Collections.singletonList(task));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(currentUser));
        when(taskRepository.findByUserId(anyInt(), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> response = taskService.getTasks(null, null, null, 0, 10, "dueDate", "asc");

        assertEquals(1, response.getTotalElements());
        assertEquals(100, response.getContent().get(0).getId());
        verify(taskRepository).findByUserId(anyInt(), any(Pageable.class));
        verify(taskRepository, never()).findAll(any(Pageable.class));
    }

    /**
     * Verifies ADMIN role can retrieve all tasks.
     */
    @Test
    void getTasks_WithAdminRole_ReturnsAllTasks() {
        setAuth("admin");

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("admin");
        currentUser.setRole(Role.ADMIN);

        Task task = buildTask(200, currentUser);
        Page<Task> taskPage = new PageImpl<Task>(Collections.singletonList(task));

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentUser));
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> response = taskService.getTasks(null, null, null, 0, 10, "priority", "desc");

        assertEquals(1, response.getTotalElements());
        assertEquals(200, response.getContent().get(0).getId());
        verify(taskRepository).findAll(any(Pageable.class));
    }

    /**
     * Verifies non-owner USER cannot access another user's task details.
     */
    @Test
    void getTaskById_WithDifferentOwner_ThrowsUnauthorized() {
        setAuth("john");

        User currentUser = new User();
        currentUser.setId(10);
        currentUser.setUsername("john");
        currentUser.setRole(Role.USER);

        User owner = new User();
        owner.setId(20);
        owner.setUsername("other");
        owner.setRole(Role.USER);

        Task task = buildTask(300, owner);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(currentUser));
        when(taskRepository.findById(300)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedActionException.class, () -> taskService.getTaskById(300));
    }

    /**
     * Helper to set a username as the current authentication principal.
     */
    private void setAuth(String username) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Helper to create a minimal task fixture for tests.
     */
    private Task buildTask(Integer id, User user) {
        Task task = new Task();
        task.setId(id);
        task.setTitle("Sample");
        task.setDescription("Sample description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setUser(user);
        return task;
    }
}

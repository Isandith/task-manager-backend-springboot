package com.taskmanager.task_manager.service.impl;

import com.taskmanager.task_manager.dto.request.TaskRequest;
import com.taskmanager.task_manager.dto.response.TaskResponse;
import com.taskmanager.task_manager.entity.Task;
import com.taskmanager.task_manager.entity.User;
import com.taskmanager.task_manager.enums.Role;
import com.taskmanager.task_manager.enums.TaskPriority;
import com.taskmanager.task_manager.enums.TaskStatus;
import com.taskmanager.task_manager.exception.BadRequestException;
import com.taskmanager.task_manager.exception.ResourceNotFoundException;
import com.taskmanager.task_manager.exception.UnauthorizedActionException;
import com.taskmanager.task_manager.repository.TaskRepository;
import com.taskmanager.task_manager.repository.UserRepository;
import com.taskmanager.task_manager.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Task service implementation with role-based access and filter logic.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a task for the authenticated user.
     *
     * @param request task payload
     * @return created task response
     */
    @Override
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(parseStatusOrDefault(request.getStatus()));
        task.setPriority(parsePriorityOrDefault(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setUser(currentUser);

        Task savedTask = taskRepository.save(task);
        return mapTaskToResponse(savedTask);
    }

    /**
     * Updates an existing task if the user is authorized.
     *
     * @param taskId task id
     * @param request updated payload
     * @return updated task response
     */
    @Override
    public TaskResponse updateTask(Integer taskId, TaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = findTaskById(taskId);

        validateTaskAccess(task, currentUser);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() != null && request.getStatus().trim().length() > 0) {
            task.setStatus(parseStatus(request.getStatus()));
        }

        if (request.getPriority() != null && request.getPriority().trim().length() > 0) {
            task.setPriority(parsePriority(request.getPriority()));
        }

        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return mapTaskToResponse(updatedTask);
    }

    /**
     * Deletes a task if the user is authorized.
     *
     * @param taskId task id
     */
    @Override
    public void deleteTask(Integer taskId) {
        User currentUser = getCurrentUser();
        Task task = findTaskById(taskId);

        validateTaskAccess(task, currentUser);
        taskRepository.delete(task);
    }

    /**
     * Retrieves a task by id if the user is authorized.
     *
     * @param taskId task id
     * @return task response
     */
    @Override
    public TaskResponse getTaskById(Integer taskId) {
        User currentUser = getCurrentUser();
        Task task = findTaskById(taskId);

        validateTaskAccess(task, currentUser);
        return mapTaskToResponse(task);
    }

    /**
     * Retrieves tasks with optional filters, paging, and sorting.
     *
     * @param status optional status filter
     * @param priority optional priority filter
     * @param page zero-based page index
     * @param size page size
     * @param sortBy sort field
     * @param sortDirection sort direction
     * @return page of task responses
     */
    @Override
    public Page<TaskResponse> getTasks(String status,
                                       String priority,
                                       int page,
                                       int size,
                                       String sortBy,
                                       String sortDirection) {
        User currentUser = getCurrentUser();

        String mappedSortBy = mapSortBy(sortBy);
        Sort.Direction direction = parseSortDirection(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, mappedSortBy));

        TaskStatus statusEnum = null;
        TaskPriority priorityEnum = null;

        if (status != null && status.trim().length() > 0) {
            statusEnum = parseStatus(status);
        }

        if (priority != null && priority.trim().length() > 0) {
            priorityEnum = parsePriority(priority);
        }

        Page<Task> taskPage = fetchTaskPageByRoleAndFilters(currentUser, statusEnum, priorityEnum, pageable);

        List<TaskResponse> responseList = new ArrayList<TaskResponse>();
        List<Task> taskList = taskPage.getContent();
        int i;
        for (i = 0; i < taskList.size(); i++) {
            responseList.add(mapTaskToResponse(taskList.get(i)));
        }

        return new PageImpl<TaskResponse>(responseList, pageable, taskPage.getTotalElements());
    }

    /**
     * Marks a task as completed if the user is authorized.
     *
     * @param taskId task id
     * @return updated task response
     */
    @Override
    public TaskResponse markTaskCompleted(Integer taskId) {
        User currentUser = getCurrentUser();
        Task task = findTaskById(taskId);

        validateTaskAccess(task, currentUser);
        task.setStatus(TaskStatus.DONE);

        Task updatedTask = taskRepository.save(task);
        return mapTaskToResponse(updatedTask);
    }

    /**
     * Fetches task pages according to role and optional status/priority filters.
     */
    private Page<Task> fetchTaskPageByRoleAndFilters(User currentUser,
                                                     TaskStatus status,
                                                     TaskPriority priority,
                                                     Pageable pageable) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (isAdmin) {
            if (status != null && priority != null) {
                return taskRepository.findByStatusAndPriority(status, priority, pageable);
            }
            if (status != null) {
                return taskRepository.findByStatus(status, pageable);
            }
            if (priority != null) {
                return taskRepository.findByPriority(priority, pageable);
            }
            return taskRepository.findAll(pageable);
        }

        Integer userId = currentUser.getId();

        if (status != null && priority != null) {
            return taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority, pageable);
        }
        if (status != null) {
            return taskRepository.findByUserIdAndStatus(userId, status, pageable);
        }
        if (priority != null) {
            return taskRepository.findByUserIdAndPriority(userId, priority, pageable);
        }
        return taskRepository.findByUserId(userId, pageable);
    }

    private String mapSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().length() == 0) {
            return "dueDate";
        }

        if ("dueDate".equalsIgnoreCase(sortBy) || "due_date".equalsIgnoreCase(sortBy)) {
            return "dueDate";
        }

        if ("priority".equalsIgnoreCase(sortBy)) {
            return "priority";
        }

        throw new BadRequestException("sortBy must be dueDate or priority");
    }

    private Sort.Direction parseSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().length() == 0) {
            return Sort.Direction.ASC;
        }

        if ("ASC".equalsIgnoreCase(sortDirection)) {
            return Sort.Direction.ASC;
        }

        if ("DESC".equalsIgnoreCase(sortDirection)) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException("sortDirection must be ASC or DESC");
    }

    /**
     * Validates whether the current user can access the given task.
     */
    private void validateTaskAccess(Task task, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new UnauthorizedActionException("You are not allowed to access this task");
        }

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to access this task");
        }
    }

    /**
     * Resolves the authenticated user from the security context.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedActionException("No authenticated user found");
        }

        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return optionalUser.get();
    }

    private Task findTaskById(Integer taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return optionalTask.get();
    }

    /**
     * Maps a task entity to API response format.
     */
    private TaskResponse mapTaskToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        if (task.getStatus() != null) {
            response.setStatus(task.getStatus().name());
        }

        if (task.getPriority() != null) {
            response.setPriority(task.getPriority().name());
        }

        if (task.getUser() != null) {
            response.setUserId(task.getUser().getId());
        }

        return response;
    }

    private TaskStatus parseStatusOrDefault(String status) {
        if (status == null || status.trim().length() == 0) {
            return TaskStatus.TODO;
        }
        return parseStatus(status);
    }

    private TaskPriority parsePriorityOrDefault(String priority) {
        if (priority == null || priority.trim().length() == 0) {
            return TaskPriority.MEDIUM;
        }
        return parsePriority(priority);
    }

    private TaskStatus parseStatus(String status) {
        String normalized = status.trim().toUpperCase();
        try {
            return TaskStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid task status. Allowed values: TODO, IN_PROGRESS, DONE");
        }
    }

    private TaskPriority parsePriority(String priority) {
        String normalized = priority.trim().toUpperCase();
        try {
            return TaskPriority.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid task priority. Allowed values: LOW, MEDIUM, HIGH");
        }
    }
}

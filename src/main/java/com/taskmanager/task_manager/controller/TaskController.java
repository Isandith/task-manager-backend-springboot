package com.taskmanager.task_manager.controller;

import com.taskmanager.task_manager.dto.request.TaskRequest;
import com.taskmanager.task_manager.dto.response.ApiMessageResponse;
import com.taskmanager.task_manager.dto.response.TaskResponse;
import com.taskmanager.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for task CRUD and task status actions.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a task for the current user.
     *
     * @param request task payload
     * @return created task data
     */
    @PostMapping
    @Operation(
            summary = "Create task",
            description = "Creates a new task for the currently authenticated user."
    )
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.CREATED);
    }

    /**
     * Updates an existing task.
     *
     * @param taskId task id
     * @param request updated task payload
     * @return updated task data
     */
    @PutMapping("/{taskId}")
    @Operation(
            summary = "Update task",
            description = "Updates an existing task by its id if the current user is authorized to modify it."
    )
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer taskId,
                                                   @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }

    /**
     * Deletes a task by id.
     *
     * @param taskId task id
     * @return success message
     */
    @DeleteMapping("/{taskId}")
        @Operation(
            summary = "Delete task",
            description = "Deletes a task by id if the current user has permission to delete it."
        )
    public ResponseEntity<ApiMessageResponse> deleteTask(@PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<ApiMessageResponse>(
                new ApiMessageResponse("Task deleted successfully"),
                HttpStatus.OK
        );
    }

    /**
     * Gets a single task by id.
     *
     * @param taskId task id
     * @return task data
     */
    @GetMapping("/{taskId}")
    @Operation(
            summary = "Get task by id",
            description = "Retrieves a single task by id for authorized users."
    )
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }

    /**
     * Gets a paginated task list with optional status/priority filtering.
     *
     * @param userId optional user id filter (ADMIN only)
     * @param status optional status filter
     * @param priority optional priority filter
     * @param page zero-based page index
     * @param size page size
     * @param sortBy sort field
     * @param sortDirection sort direction
     * @return paginated tasks
     */
    @GetMapping
        @Operation(
            summary = "List tasks",
            description = "Returns a paginated list of tasks with optional status and priority filters, including sorting controls. ADMIN can optionally filter by userId."
        )
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<TaskResponse> response = taskService.getTasks(userId, status, priority, page, size, sortBy, sortDirection);
        return new ResponseEntity<Page<TaskResponse>>(response, HttpStatus.OK);
    }

    /**
     * Marks a task as completed.
     *
     * @param taskId task id
     * @return updated task data
     */
    @PatchMapping("/{taskId}/complete")
    @Operation(
            summary = "Mark task completed",
            description = "Marks a task as completed by setting its status to DONE."
    )
    public ResponseEntity<TaskResponse> markTaskCompleted(@PathVariable Integer taskId) {
        TaskResponse response = taskService.markTaskCompleted(taskId);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }
}

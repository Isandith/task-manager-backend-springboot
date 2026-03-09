package com.taskmanager.task_manager.service;

import org.springframework.data.domain.Page;

import com.taskmanager.task_manager.dto.request.TaskRequest;
import com.taskmanager.task_manager.dto.response.TaskResponse;

/**
 * Contract for task management operations.
 */
public interface TaskService {

    /**
     * Creates a new task for the currently authenticated user.
     *
     * @param request task payload
     * @return created task data
     */
    TaskResponse createTask(TaskRequest request);

    /**
     * Updates an existing task.
     *
     * @param taskId id of the task to update
     * @param request updated task payload
     * @return updated task data
     */
    TaskResponse updateTask(Integer taskId, TaskRequest request);

    /**
     * Deletes a task by id.
     *
     * @param taskId id of the task to delete
     */
    void deleteTask(Integer taskId);

    /**
     * Retrieves a single task by id.
     *
     * @param taskId id of the task
     * @return task data
     */
    TaskResponse getTaskById(Integer taskId);

    /**
     * Retrieves a paginated list of tasks with optional filters.
     *
     * @param userId optional user id filter (ADMIN only)
     * @param status optional task status filter
     * @param priority optional task priority filter
     * @param page zero-based page index
     * @param size page size
     * @param sortBy field name used for sorting
     * @param sortDirection sort order (asc or desc)
     * @return page of task data
     */
    Page<TaskResponse> getTasks(Integer userId,
                                String status,
                                String priority,
                                int page,
                                int size,
                                String sortBy,
                                String sortDirection);

    /**
     * Marks the selected task as completed.
     *
     * @param taskId id of the task to complete
     * @return updated task data
     */
    TaskResponse markTaskCompleted(Integer taskId);
}

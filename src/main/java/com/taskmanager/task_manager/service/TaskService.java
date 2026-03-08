package com.taskmanager.task_manager.service;

import com.taskmanager.task_manager.dto.request.TaskRequest;
import com.taskmanager.task_manager.dto.response.TaskResponse;
import org.springframework.data.domain.Page;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse updateTask(Integer taskId, TaskRequest request);

    void deleteTask(Integer taskId);

    TaskResponse getTaskById(Integer taskId);

    Page<TaskResponse> getTasks(String status,
                                String priority,
                                int page,
                                int size,
                                String sortBy,
                                String sortDirection);

    TaskResponse markTaskCompleted(Integer taskId);
}

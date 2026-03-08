package com.taskmanager.task_manager.controller;

import com.taskmanager.task_manager.dto.request.TaskRequest;
import com.taskmanager.task_manager.dto.response.ApiMessageResponse;
import com.taskmanager.task_manager.dto.response.TaskResponse;
import com.taskmanager.task_manager.service.TaskService;
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

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer taskId,
                                                   @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiMessageResponse> deleteTask(@PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<ApiMessageResponse>(
                new ApiMessageResponse("Task deleted successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<TaskResponse> response = taskService.getTasks(status, priority, page, size, sortBy, sortDirection);
        return new ResponseEntity<Page<TaskResponse>>(response, HttpStatus.OK);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> markTaskCompleted(@PathVariable Integer taskId) {
        TaskResponse response = taskService.markTaskCompleted(taskId);
        return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
    }
}

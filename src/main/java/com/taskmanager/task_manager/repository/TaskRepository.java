package com.taskmanager.task_manager.repository;

import com.taskmanager.task_manager.entity.Task;
import com.taskmanager.task_manager.enums.TaskPriority;
import com.taskmanager.task_manager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access layer for task entities and common filtered queries.
 */
public interface TaskRepository extends JpaRepository<Task, Integer> {

    /**
     * Finds tasks for a specific user.
     */
    Page<Task> findByUserId(Integer userId, Pageable pageable);

    /**
     * Finds tasks by status.
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    /**
     * Finds tasks by priority.
     */
    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    /**
     * Finds tasks by both status and priority.
     */
    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    /**
     * Finds tasks for a user with a given status.
     */
    Page<Task> findByUserIdAndStatus(Integer userId, TaskStatus status, Pageable pageable);

    /**
     * Finds tasks for a user with a given priority.
     */
    Page<Task> findByUserIdAndPriority(Integer userId, TaskPriority priority, Pageable pageable);

    /**
     * Finds tasks for a user filtered by both status and priority.
     */
    Page<Task> findByUserIdAndStatusAndPriority(Integer userId, TaskStatus status, TaskPriority priority, Pageable pageable);
}

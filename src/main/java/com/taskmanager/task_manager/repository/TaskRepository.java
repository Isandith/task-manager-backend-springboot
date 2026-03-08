package com.taskmanager.task_manager.repository;

import com.taskmanager.task_manager.entity.Task;
import com.taskmanager.task_manager.enums.TaskPriority;
import com.taskmanager.task_manager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Page<Task> findByUserId(Integer userId, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    Page<Task> findByUserIdAndStatus(Integer userId, TaskStatus status, Pageable pageable);

    Page<Task> findByUserIdAndPriority(Integer userId, TaskPriority priority, Pageable pageable);

    Page<Task> findByUserIdAndStatusAndPriority(Integer userId, TaskStatus status, TaskPriority priority, Pageable pageable);
}

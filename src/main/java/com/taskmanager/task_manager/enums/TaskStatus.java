package com.taskmanager.task_manager.enums;

/**
 * Supported lifecycle states for a task.
 */
public enum TaskStatus {
    /** Task is created and not started yet. */
    TODO,
    /** Task is actively being worked on. */
    IN_PROGRESS,
    /** Task has been completed. */
    DONE
}

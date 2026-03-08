package com.taskmanager.task_manager.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a resource-not-found exception with a message.
     *
     * @param message error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

package com.taskmanager.task_manager.exception;

/**
 * Exception thrown when request input is invalid.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a bad request exception with a message.
     *
     * @param message error message
     */
    public BadRequestException(String message) {
        super(message);
    }
}

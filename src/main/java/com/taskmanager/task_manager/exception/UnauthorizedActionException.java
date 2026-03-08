package com.taskmanager.task_manager.exception;

/**
 * Exception thrown when a user attempts an action they are not allowed to perform.
 */
public class UnauthorizedActionException extends RuntimeException {

    /**
     * Creates an unauthorized-action exception with a message.
     *
     * @param message error message
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }
}

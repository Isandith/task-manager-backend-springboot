package com.taskmanager.task_manager.repository;

import com.taskmanager.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data access layer for user entities.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by username.
     *
     * @param username username to search
     * @return matching user if present
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email email to search
     * @return matching user if present
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username already exists.
     *
     * @param username username to validate
     * @return true when username is already used
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email already exists.
     *
     * @param email email to validate
     * @return true when email is already used
     */
    boolean existsByEmail(String email);
}

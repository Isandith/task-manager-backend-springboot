package com.taskmanager.task_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Task Manager Spring Boot application.
 */
@SpringBootApplication
public class TaskManagerApplication {

	/**
	 * Bootstraps and starts the application context.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TaskManagerApplication.class, args);
	}

}

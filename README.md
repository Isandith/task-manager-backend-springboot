# Mini Task Management System - Backend Documentation

This document is prepared for the assignment requirements and covers only the backend (Spring Boot) section.

## Project Overview

Mini Task Management System backend provides secure REST APIs for:

- User registration and login
- JWT-based authentication
- Role-based authorization (ADMIN, USER)
- Task creation, update, delete, and retrieval
- Task filtering, pagination, and sorting
- Marking tasks as completed

Business access rules:

- Only authenticated users can access task APIs.
- ADMIN can view and manage all tasks.
- USER can manage only their own tasks.

## Backend Technology Stack

- Java 17
- Spring Boot 3
- Spring Security
- JWT (JJWT)
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- Spring Validation
- Springdoc OpenAPI (Swagger UI)

## Backend Architecture (Monolith, Clean Layered Structure)

Source structure:

```text
src/main/java/com/taskmanager/task_manager/
  config/       Security and OpenAPI configuration
  controller/   REST API endpoints
  dto/          Request and response models
  entity/       JPA entities (database mapping)
  enums/        Role, TaskStatus, TaskPriority
  exception/    Custom exceptions and global exception handler
  repository/   Data access layer (JPA repositories)
  security/     JWT service/filter, UserDetailsService, auth entry point
  service/      Business interfaces
  service/impl/ Business logic implementations
```

Layer responsibilities:

- Controller layer
  - Handles HTTP requests and responses
  - Uses request validation via Valid annotation
- Service layer
  - Contains business logic and authorization checks
- Repository layer
  - Provides database access with query methods
- Entity layer
  - Maps application objects to relational tables
- Security layer
  - Handles token generation, token validation, and authentication context
- Exception layer
  - Centralizes API error handling and status codes

## Authentication and Authorization

### JWT Authentication Flow

1. Client calls login endpoint with username and password.
2. Spring AuthenticationManager validates credentials.
3. Backend generates JWT token.
4. Client sends token in Authorization header as Bearer token.
5. JWT filter validates token and sets authenticated user in security context.

### Role-Based Access Control

- Public endpoints:
  - POST /api/v1/auth/register
  - POST /api/v1/auth/login
  - Swagger/OpenAPI endpoints
- ADMIN only endpoints:
  - POST /api/v1/auth/register-admin
  - GET /api/v1/users
- Authenticated endpoints:
  - All task endpoints under /api/v1/tasks

Task ownership enforcement:

- ADMIN can access all tasks.
- USER can access only tasks where task.userId matches the logged-in user.

## Database Configuration

The backend is configured through environment variables in application.properties.

Current database setup:

- Driver: PostgreSQL
- Hibernate DDL: validate
- Schema is expected to exist before application startup

## Environment Variables (Required Key Names)

Use environment variables or a root .env file.

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION_MS=86400000
```

Notes:

- JWT_SECRET must be a Base64-encoded secret key.
- JWT_EXPIRATION_MS default is 86400000 ms (24 hours).

## Database Schema

The project uses two core tables: users and tasks.

### users table

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | SERIAL | PRIMARY KEY | User ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Username |
| email | VARCHAR(100) | UNIQUE, NOT NULL | Email |
| password | VARCHAR(255) | NOT NULL | Encrypted password |
| role | VARCHAR(20) | NOT NULL | ADMIN or USER |
| created_at | TIMESTAMP |  | Created timestamp |
| updated_at | TIMESTAMP |  | Updated timestamp |

### tasks table

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | SERIAL | PRIMARY KEY | Task ID |
| title | VARCHAR(200) | NOT NULL | Task title |
| description | TEXT |  | Task description |
| status | VARCHAR(20) |  | TODO, IN_PROGRESS, DONE |
| priority | VARCHAR(20) |  | LOW, MEDIUM, HIGH |
| due_date | DATE |  | Task due date |
| created_at | TIMESTAMP |  | Created timestamp |
| updated_at | TIMESTAMP |  | Updated timestamp |
| user_id | INT | FOREIGN KEY users(id), ON DELETE CASCADE | Task owner |

Relationship:

- One user has many tasks.
- One task belongs to one user.

### SQL Script (Assignment Reference)

```sql
CREATE DATABASE task_management_db;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20),
    priority VARCHAR(20),
    due_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id INT,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE USER task_user_admin WITH PASSWORD 'Admin_task';

GRANT ALL PRIVILEGES ON DATABASE task_management_db TO task_user_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO task_user_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO task_user_admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO task_user_admin;

INSERT INTO users (
    username,
    email,
    password,
    role,
    created_at,
    updated_at
)
VALUES (
    'admin',
    'admin@example.com',
    '$2a$12$9YtusP/JjonGMPtetioUru.v98sSAM.MIOAdKbxnqVLpwY8d2Sev6',
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

Seed admin login for testing:

- Username: admin
- Password: Admin123?

## API Documentation

Base URL: /api/v1

### Auth APIs

#### POST /auth/register

Description:
Register a USER account.

Request Body:

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "role": "USER"
}
```

Response:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "username": "john",
  "role": "USER"
}
```

Authorization: Public

#### POST /auth/register-admin

Description:
Register an ADMIN account.

Authorization: ADMIN only

#### POST /auth/login

Description:
Authenticate user and return JWT.

Request Body:

```json
{
  "username": "john",
  "password": "secret123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "username": "john",
  "role": "USER"
}
```

Authorization: Public

### User API

#### GET /users

Description:
Get all users.

Authorization: ADMIN only

### Task APIs (All require Bearer token)

#### POST /tasks

Description:
Create task for current user.

Request Body:

```json
{
  "title": "Prepare report",
  "description": "Weekly sprint report",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-03-20"
}
```

Authorization: Authenticated user

#### PUT /tasks/{taskId}

Description:
Update existing task.

Authorization:
- ADMIN: any task
- USER: own task only

#### DELETE /tasks/{taskId}

Description:
Delete task.

Authorization:
- ADMIN: any task
- USER: own task only

#### GET /tasks/{taskId}

Description:
Get task by id.

Authorization:
- ADMIN: any task
- USER: own task only

#### GET /tasks

Description:
List tasks with filter, pagination, and sorting.

Query params:

- userId (optional, ADMIN use case)
- status (TODO, IN_PROGRESS, DONE)
- priority (LOW, MEDIUM, HIGH)
- page (default 0)
- size (default 10)
- sortBy (dueDate or priority)
- sortDirection (asc or desc)

Authorization: Authenticated user

#### PATCH /tasks/{taskId}/complete

Description:
Mark task as DONE.

Authorization:
- ADMIN: any task
- USER: own task only

## Task Features Coverage (Assignment Mapping)

Implemented:

- Create task
- Update task
- Delete task
- View single task
- View task list
- Mark task as completed
- Filter by status
- Filter by priority
- Pagination
- Sorting by due date or priority

## Input Validation

Implemented with Jakarta Validation on request DTOs:

- NotBlank
- Size
- Email

Examples:

- username required and length restricted
- email required and format validated
- password required and minimum length
- task title required

Validation failures return 400 with field-level messages.

## Exception Handling

Centralized with global exception handler.

Handled statuses:

- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 500 Internal Server Error

Common error cases:

- Invalid request body
- Invalid credentials
- Unauthorized resource access
- Resource not found
- Unexpected server/database errors

## Setup Instructions (Backend)

1. Install Java 17 and Maven.
2. Create PostgreSQL database and run schema script.
3. Configure environment variables.
4. Start backend.

Run commands:

```bash
mvn spring-boot:run
```

Windows wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger URL:

- http://localhost:8080/swagger-ui/index.html

## Repository and Submission Notes

- Keep secrets only in environment variables.
- Do not commit build folders or dependency folders.
- Recommended exclusions:
  - target/
  - node_modules/
  - .next/

This backend documentation section is aligned with the assignment requirements and can be used directly in the repository README.

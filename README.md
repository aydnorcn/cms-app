# Community Management System

This is a Community Management System application built with Java and Spring Boot. It provides functionalities for managing roles, users, posts, polls, comments and more.
## Table of Contents

- [Technologies](#technologies)
- [Features](#features)
- [Architecture](#architecture)
- [Setup](#setup)
- [Usage](#usage)
- [Endpoints](#endpoints)

## Technologies

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- JWT
- Docker

## Features
  - **Task Management**: Admins and moderators can assign tasks to specific users.
  - **Blog System**: Users can create blog posts with category support.
  - **Commenting System**: Users can comment on posts and replies.
  - **Event Management**: Admins, moderators and organizators can create and manage community events.
  - **Survey System**: Admins, moderators and organizators can create polls for users to participate in.
  - **Event Registration**: Users can register for events.
  - **Role-Based Access Control**: Admins, moderators, and regular users have different levels of access.
  - **User Authentication**: JWT-based authentication

## Architecture

The application follows a layered architecture with the following main components:

- **Controller Layer**: Handles HTTP requests and responses. It includes controllers for roles, authentication, food items, orders, and more.
- **Service Layer**: Contains business logic and interacts with the repository layer.
- **Repository Layer**: Manages data persistence and retrieval using Spring Data JPA.
- **DTOs (Data Transfer Objects)**: Used to transfer data between the layers.
- **Entities**: Represent the database tables.

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/aydnorcn/cms-app.git
    cd cms-app
    ```

2. Install the dependencies:
    ```sh
    mvn clean install
    ```

3. Configure the database:
    - Update the `env.properties` file with your PostgreSQL database credentials and JWT key.


4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Usage

To use the application, you can send HTTP requests to the endpoints defined in the controllers. You can use tools like Postman or cURL to interact with the API.

### Authentication

Before accessing protected endpoints, you need to authenticate and obtain a JWT token.

1. **Register a new user**:
    ```sh
    POST /api/auth/register
    ```

2. **Login**:
    ```sh
    POST /api/auth/login
    ```

Use the obtained JWT token in the `Authorization` header for subsequent requests.

## Endpoints

Here are some of the main endpoints available in the application:

- **Roles**
    - `GET /api/roles/{id}` - Retrieve role by id
    - `POST /api/roles` - Create a new role
    - `PUT /api/roles/{id}` - Update role name by id
    - `DELETE /api/roles/{id}` - Delete role by id

- **Auth**
    - `POST /api/auth/login` - Login to system for get access token
    - `POST /api/auth/register` - Register a new user
    - `POST /api/auth/refresh` - Refresh access token with refresh token

- **Posts**
    - `GET /api/posts` - "Retrieve posts by filtering and pagination
    - `GET /api/posts/{id}` - Retrieve post by id
    - `POST /api/posts` - Create a new post
    - `PUT /api/posts/{id}` - Update all required parts of post
    - `PATCH /api/posts/{id}` - Update partial parts of post
    - `POST /api/posts/{id}/approve` - Approve post
    - `DELETE /api/posts/{id}` - Delete post by id
 
- **Events**
    - `GET /api/events` - Retrieve events by filtering and pagination
    - `GET /api/events/{id}` - Retrieve event by id
    - `POST /api/events` - Create a new event
    - `PUT /api/events/{id}` - Update all required parts of event
    - `PATCH /api/events/{id}` - Update partial parts of event
    - `DELETE /api/events/{id}` - Delete event by id

- **Polls**
    - `GET /api/polls` - Retrieve posts by filtering and pagination
    - `GET /api/polls/{id}` - Retrieve poll by id
    - `POST /api/polls` - Create a new poll
    - `PUT /api/polls/{id}` - Update all required parts of poll
    - `PATCH /api/polls/{id}` - Update partial parts of poll
    - `DELETE /api/polls/{id}` - Delete poll by id

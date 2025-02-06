CREATE TABLE IF NOT EXISTS users_roles
(
    user_id VARCHAR(255),
    role_id VARCHAR(255),
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT ur_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT ur_fk_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
);
CREATE TABLE refresh_tokens
(
    id              VARCHAR(255) PRIMARY KEY,
    token           VARCHAR(255) NOT NULL UNIQUE,
    expiration_time TIMESTAMP,
    user_id         VARCHAR(255) UNIQUE,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);
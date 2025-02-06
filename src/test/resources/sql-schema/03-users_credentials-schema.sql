CREATE TABLE IF NOT EXISTS user_credentials
(
    id       VARCHAR(255) PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_id  VARCHAR(255),

    CONSTRAINT uc_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);
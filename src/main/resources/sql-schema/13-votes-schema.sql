CREATE TABLE IF NOT EXISTS votes
(
    id         VARCHAR(255) PRIMARY KEY,
    user_id    VARCHAR(255),
    option_id  VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT v_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT v_fk_option
        FOREIGN KEY (option_id)
            REFERENCES options (id)
);
CREATE TABLE IF NOT EXISTS likes
(
    id         VARCHAR(255) PRIMARY KEY,
    user_id    VARCHAR(255),
    post_id    VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT l_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT l_fk_post
        FOREIGN KEY (post_id)
            REFERENCES posts (id)
);
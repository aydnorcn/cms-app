CREATE TABLE IF NOT EXISTS posts
(
    id          VARCHAR(255) PRIMARY KEY,
    title       VARCHAR(255),
    content     TEXT,
    status      VARCHAR(255),
    author_id   VARCHAR(255),
    category_id VARCHAR(255),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT p_fk_author
        FOREIGN KEY (author_id)
            REFERENCES users (id),
    CONSTRAINT p_fk_category
        FOREIGN KEY (category_id)
            REFERENCES categories (id)
);
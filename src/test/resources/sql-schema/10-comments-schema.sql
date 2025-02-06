CREATE TABLE IF NOT EXISTS post_comments
(
    id         VARCHAR(255) PRIMARY KEY,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    post_id    VARCHAR(255),
    CONSTRAINT pc_fk_post
        FOREIGN KEY (post_id)
            REFERENCES posts (id)
);

CREATE TABLE IF NOT EXISTS reply_comments
(
    id                VARCHAR(255) PRIMARY KEY,
    content           TEXT         NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    created_by        VARCHAR(255) NOT NULL,
    parent_comment_id VARCHAR(255),
    CONSTRAINT rc_fk_parent_comment
        FOREIGN KEY (parent_comment_id)
            REFERENCES post_comments (id)
);
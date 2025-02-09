CREATE TABLE IF NOT EXISTS assignments
(
    id           VARCHAR(255) PRIMARY KEY,
    event_id     VARCHAR(255),
    title        VARCHAR(255),
    content      TEXT,
    is_completed BOOLEAN,
    priority     INT,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(255) NOT NULL,
    updated_by   VARCHAR(255),
    CONSTRAINT a_fk_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS assignment_users
(
    assignment_id VARCHAR(255),
    user_id       VARCHAR(255),
    PRIMARY KEY (assignment_id, user_id),
    CONSTRAINT au_fk_assignment
        FOREIGN KEY (assignment_id)
            REFERENCES assignments (id),
    CONSTRAINT au_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

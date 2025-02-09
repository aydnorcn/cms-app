CREATE TABLE IF NOT EXISTS polls
(
    id             VARCHAR(255) PRIMARY KEY,
    title          VARCHAR(255),
    description    TEXT,
    type           VARCHAR(255),
    active         BOOLEAN,
    max_vote_count INT,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    created_by     VARCHAR(255) NOT NULL,
    updated_by     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS options
(
    id         VARCHAR(255) PRIMARY KEY,
    text       VARCHAR(255),
    poll_id    VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    CONSTRAINT o_fk_poll
        FOREIGN KEY (poll_id)
            REFERENCES polls (id)
);
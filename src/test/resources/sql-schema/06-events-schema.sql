CREATE TABLE IF NOT EXISTS events
(
    id          VARCHAR(255) PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    location    VARCHAR(255),
    date        DATE,
    start_time  TIME,
    end_time    TIME,
    status      VARCHAR(255),
    created_by  VARCHAR(255) NOT NULL,
    updated_by  VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
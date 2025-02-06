CREATE TABLE IF NOT EXISTS event_registrations
(
    id         VARCHAR(255) PRIMARY KEY,
    user_id    VARCHAR(255),
    event_id   VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT er_fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT er_fk_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
);
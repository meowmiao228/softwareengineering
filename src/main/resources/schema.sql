CREATE TABLE IF NOT EXISTS teaching_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subject_code VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS teaching_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    role VARCHAR(32) NOT NULL,
    content CLOB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_session FOREIGN KEY (session_id) REFERENCES teaching_session (id)
);

CREATE INDEX IF NOT EXISTS idx_message_session_created ON teaching_message (session_id, created_at);

CREATE TABLE coffee_chats (
    coffee_chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at DATETIME NULL,
    left_at DATETIME NULL,
    CONSTRAINT fk_coffee_chats_student FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT fk_coffee_chats_instructor FOREIGN KEY (instructor_id) REFERENCES users(user_id),
    CONSTRAINT fk_coffee_chats_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE coffee_chat_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coffee_chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coffee_chat_messages_chat FOREIGN KEY (coffee_chat_id) REFERENCES coffee_chats(coffee_chat_id),
    CONSTRAINT fk_coffee_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users(user_id)
);

-- 목록 조회/정렬 최적화
CREATE INDEX idx_coffee_chats_student_status ON coffee_chats(student_id, status);
CREATE INDEX idx_coffee_chats_instructor_status ON coffee_chats(instructor_id, status);
CREATE INDEX idx_coffee_chat_messages_chat_created ON coffee_chat_messages(coffee_chat_id, created_at);

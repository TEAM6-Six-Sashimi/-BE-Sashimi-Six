CREATE TABLE cover_letters (
                               cover_letter_id BIGINT NOT NULL AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               question_key VARCHAR(50) NOT NULL,
                               content TEXT NULL,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME NULL,
                               PRIMARY KEY (cover_letter_id),
                               UNIQUE KEY uk_cover_letters_user_question (user_id, question_key),
                               INDEX idx_cover_letters_user_id (user_id)
);
CREATE TABLE payment_idempotencies (
                                       payment_idempotency_id BIGINT NOT NULL AUTO_INCREMENT,
                                       user_id BIGINT NOT NULL,
                                       idempotency_key VARCHAR(100) NOT NULL,
                                       request_fingerprint VARCHAR(200) NOT NULL,
                                       status VARCHAR(20) NOT NULL,
                                       result_json TEXT NULL,
                                       created_at DATETIME(6) NOT NULL,
                                       updated_at DATETIME(6) NOT NULL,

                                       PRIMARY KEY (payment_idempotency_id),

                                       CONSTRAINT uq_payment_idempotencies_user_key
                                           UNIQUE (user_id, idempotency_key),

                                       CONSTRAINT fk_payment_idempotencies_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(user_id),

                                       INDEX idx_payment_idempotencies_created_at (
        created_at
    )
);
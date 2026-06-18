CREATE TABLE credit_charge_payments (
                                        credit_charge_payment_id BIGINT NOT NULL AUTO_INCREMENT,
                                        user_id BIGINT NOT NULL,
                                        order_id VARCHAR(64) NOT NULL,
                                        payment_key VARCHAR(200) NULL,
                                        amount BIGINT NOT NULL,
                                        status VARCHAR(30) NOT NULL,
                                        failure_reason VARCHAR(500) NULL,
                                        requested_at DATETIME(6) NOT NULL,
                                        approved_at DATETIME(6) NULL,
                                        PRIMARY KEY (credit_charge_payment_id),
                                        UNIQUE KEY uq_credit_charge_payments_order_id (order_id),
                                        UNIQUE KEY uq_credit_charge_payments_payment_key (payment_key),
                                        KEY idx_credit_charge_payments_user_id (user_id)
);
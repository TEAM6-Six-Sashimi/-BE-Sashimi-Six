CREATE INDEX idx_payments_user_created_id
    ON payments (user_id, created_at, payment_id);

CREATE INDEX idx_order_items_order_type
    ON order_items (order_id, item_type);

CREATE INDEX idx_subscription_payments_user_paid_id
    ON subscription_payments (user_id, paid_at, subscription_payment_id);

CREATE INDEX idx_credit_charge_payments_user_status_approved_id
    ON credit_charge_payments (user_id, status, approved_at, credit_charge_payment_id);
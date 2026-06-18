-- ============================================================
-- V2.1: Module 4 cart/order item generalization
-- Supports COURSE and AI_SUBSCRIPTION items while preserving
-- existing course_id columns for backward compatibility.
-- ============================================================

ALTER TABLE cart_items
    ADD COLUMN item_type VARCHAR(30) NOT NULL DEFAULT 'COURSE',
    ADD COLUMN item_id BIGINT NULL;

UPDATE cart_items
SET item_id = course_id
WHERE item_id IS NULL;

ALTER TABLE cart_items
DROP FOREIGN KEY fk_cart_course;

-- fk_cart_user needs an index that starts with user_id.
-- The old unique index uq_cart_user_course was serving that role,
-- so create a replacement before dropping it.
ALTER TABLE cart_items
    ADD INDEX idx_cart_items_user_id (user_id);

ALTER TABLE cart_items
DROP INDEX uq_cart_user_course;

ALTER TABLE cart_items
    MODIFY COLUMN item_id BIGINT NOT NULL,
    MODIFY COLUMN course_id BIGINT NULL;

ALTER TABLE cart_items
    ADD CONSTRAINT uq_cart_user_item UNIQUE (user_id, item_type, item_id);

ALTER TABLE order_items
    ADD COLUMN item_type VARCHAR(30) NOT NULL DEFAULT 'COURSE',
    ADD COLUMN item_id BIGINT NULL;

UPDATE order_items
SET item_id = course_id
WHERE item_id IS NULL;

ALTER TABLE order_items
DROP FOREIGN KEY fk_order_items_course;

ALTER TABLE order_items
    MODIFY COLUMN item_id BIGINT NOT NULL,
    MODIFY COLUMN course_id BIGINT NULL;
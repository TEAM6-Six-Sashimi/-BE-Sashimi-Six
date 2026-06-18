-- Cart is course-only.
-- OrderItem remains generalized for COURSE and AI_SUBSCRIPTION.

UPDATE cart_items
SET course_id = item_id
WHERE course_id IS NULL
  AND item_type = 'COURSE';

ALTER TABLE cart_items
DROP INDEX uq_cart_user_item;

ALTER TABLE cart_items
    MODIFY COLUMN course_id BIGINT NOT NULL;

ALTER TABLE cart_items
    ADD CONSTRAINT uq_cart_user_course
        UNIQUE (user_id, course_id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_course
        FOREIGN KEY (course_id)
            REFERENCES courses(course_id);

ALTER TABLE cart_items
DROP COLUMN item_type,
    DROP COLUMN item_id;
ALTER TABLE courses
DROP FOREIGN KEY fk_courses_ncs_info;

DROP INDEX idx_courses_ncs_info_id ON courses;

ALTER TABLE courses
DROP COLUMN ncs_info_id;

ALTER TABLE categories
    ADD COLUMN ncs_info_id BIGINT NULL;

CREATE INDEX idx_categories_ncs_info_id ON categories(ncs_info_id);

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_ncs_info
        FOREIGN KEY (ncs_info_id) REFERENCES ncs_info(ncs_info_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
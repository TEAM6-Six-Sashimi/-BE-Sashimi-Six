ALTER TABLE courses
    ADD CONSTRAINT fk_courses_ncs_info
        FOREIGN KEY (ncs_info_id) REFERENCES ncs_info(ncs_info_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
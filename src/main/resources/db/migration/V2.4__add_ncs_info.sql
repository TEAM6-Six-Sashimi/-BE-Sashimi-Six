CREATE TABLE ncs_info (
                          ncs_info_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          ncs_code VARCHAR(50) NOT NULL UNIQUE,
                          category_path VARCHAR(500),
                          job_name VARCHAR(255),
                          job_description TEXT,
                          ability_unit_code VARCHAR(50),
                          ability_unit_name VARCHAR(255),
                          ability_unit_description TEXT,
                          last_synced_at DATETIME
);

ALTER TABLE courses
    ADD COLUMN ncs_info_id BIGINT NULL;

CREATE INDEX idx_courses_ncs_info_id ON courses(ncs_info_id);
ALTER TABLE instructor_profiles
    ADD COLUMN profile_image_path VARCHAR(500) NULL,
    ADD COLUMN resume_file_path VARCHAR(500) NULL,
    ADD COLUMN motivation_letter TEXT NULL,
    ADD COLUMN category_id BIGINT NULL,
    ADD COLUMN rejection_category VARCHAR(50) NULL,
    ADD COLUMN rejection_reason VARCHAR(100) NULL,
    ADD COLUMN main_careers JSON NULL;

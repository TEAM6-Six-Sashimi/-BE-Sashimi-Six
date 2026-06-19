ALTER TABLE instructor_profiles
    ADD COLUMN motivation_letter TEXT NULL,
    ADD COLUMN category_id       BIGINT NULL,
    ADD COLUMN profile_image_path VARCHAR(500) NULL,
    ADD COLUMN resume_file_path   VARCHAR(500) NULL,
    ADD COLUMN main_careers       JSON NULL;
ALTER TABLE instructor_application_certifications
    MODIFY COLUMN certification_name VARCHAR(255) NULL,
    ADD COLUMN certification_number VARCHAR(50) NULL,
    ADD COLUMN verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

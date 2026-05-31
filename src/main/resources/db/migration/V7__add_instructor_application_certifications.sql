CREATE TABLE IF NOT EXISTS instructor_application_certifications (
    certification_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_profile_id  BIGINT NOT NULL,
    certification_name     VARCHAR(255) NOT NULL,
    issued_by              VARCHAR(255),
    CONSTRAINT fk_instr_cert_profile FOREIGN KEY (instructor_profile_id)
        REFERENCES instructor_profiles(instructor_profile_id) ON DELETE CASCADE
);

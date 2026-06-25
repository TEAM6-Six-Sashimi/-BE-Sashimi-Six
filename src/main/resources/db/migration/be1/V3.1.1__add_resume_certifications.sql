CREATE TABLE resume_certifications (
                                       resume_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       resume_id BIGINT NOT NULL,

                                       name VARCHAR(255) NOT NULL,
                                       type VARCHAR(30) NOT NULL,
                                       issuer VARCHAR(255) NOT NULL,
                                       acquired_date DATE NOT NULL,
                                       score_or_grade VARCHAR(100) NULL,

                                       certification_order INT NOT NULL,

                                       CONSTRAINT fk_resume_certifications_resume
                                           FOREIGN KEY (resume_id)
                                               REFERENCES resumes(resume_id)
                                               ON DELETE CASCADE,

                                       INDEX idx_resume_certification_order (
        resume_id,
        certification_order
    )
);
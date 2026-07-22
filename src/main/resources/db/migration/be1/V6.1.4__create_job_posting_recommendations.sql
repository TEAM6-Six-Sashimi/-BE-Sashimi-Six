CREATE TABLE job_posting_recommendations (
                                             recommendation_id BIGINT NOT NULL AUTO_INCREMENT,
                                             user_id BIGINT NOT NULL,
                                             resume_id BIGINT NULL,

                                             input_type VARCHAR(20) NOT NULL,
                                             source_url VARCHAR(1000) NULL,
                                             raw_content MEDIUMTEXT NULL,
                                             resume_content MEDIUMTEXT NULL,

                                             resume_based BOOLEAN NOT NULL,
                                             analysis_status VARCHAR(30) NOT NULL,

                                             summary_json JSON NULL,
                                             fit_analysis_json JSON NULL,
                                             courses_json JSON NULL,
                                             certificates_json JSON NULL,

                                             created_at DATETIME(6) NOT NULL,

                                             PRIMARY KEY (recommendation_id),
                                             INDEX idx_job_posting_recommendations_user_created_at (user_id, created_at),
                                             INDEX idx_job_posting_recommendations_user_id (user_id),
                                             INDEX idx_job_posting_recommendations_status (analysis_status)
);
CREATE TABLE qualification_exam_schedules (
                                              qualification_exam_schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                              qualification_name VARCHAR(255) NOT NULL,
                                              jm_cd VARCHAR(20) NOT NULL,

                                              impl_year INT NOT NULL,
                                              impl_seq INT NOT NULL,

                                              qualification_type_code VARCHAR(10) NULL,
                                              qualification_type_name VARCHAR(100) NULL,

                                              doc_reg_start_date DATE NULL,
                                              doc_reg_end_date DATE NULL,
                                              doc_exam_start_date DATE NULL,
                                              doc_exam_end_date DATE NULL,

                                              last_synced_at DATETIME NOT NULL,

                                              UNIQUE KEY uq_qualification_exam_schedule (
                                                  jm_cd,
                                                  impl_year,
                                                  impl_seq
                                                  ),

                                              INDEX idx_qualification_exam_next (
        qualification_name,
        doc_exam_start_date
    )
);
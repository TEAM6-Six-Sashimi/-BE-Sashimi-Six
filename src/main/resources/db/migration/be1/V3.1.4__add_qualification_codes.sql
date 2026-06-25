CREATE TABLE qualification_codes (
                                     qualification_code_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                     jm_cd VARCHAR(20) NOT NULL,
                                     qualification_name VARCHAR(255) NOT NULL,

                                     qualification_type_code VARCHAR(10) NOT NULL,
                                     qualification_type_name VARCHAR(100) NOT NULL,

                                     series_code VARCHAR(20) NULL,
                                     series_name VARCHAR(100) NULL,

                                     major_field_code VARCHAR(20) NULL,
                                     major_field_name VARCHAR(100) NULL,

                                     middle_field_code VARCHAR(20) NULL,
                                     middle_field_name VARCHAR(100) NULL,

                                     last_synced_at DATETIME NOT NULL,

                                     UNIQUE KEY uq_qualification_codes_jm_cd (jm_cd),

                                     INDEX idx_qualification_codes_name (
        qualification_name
    )
);
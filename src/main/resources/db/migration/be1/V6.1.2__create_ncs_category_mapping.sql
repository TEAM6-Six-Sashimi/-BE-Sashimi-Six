CREATE TABLE ncs_category_mapping (
                                      ncs_category_mapping_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      category_name VARCHAR(100) NOT NULL,
                                      duty_cd VARCHAR(20) NOT NULL,
                                      ncs_job_name VARCHAR(100) NOT NULL,
                                      active BOOLEAN NOT NULL DEFAULT TRUE,
                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME NULL,

                                      UNIQUE KEY uk_ncs_category_mapping_category_name (category_name),
                                      INDEX idx_ncs_category_mapping_duty_cd (duty_cd),
                                      INDEX idx_ncs_category_mapping_active (active)
);

INSERT INTO ncs_category_mapping (category_name, duty_cd, ncs_job_name, active)
VALUES
    ('정보처리기사', '20010202', '응용SW엔지니어링', TRUE),
    ('정보보안기사', '20010206', '보안엔지니어링', TRUE),
    ('네트워크관리사', '20010205', 'NW엔지니어링', TRUE),
    ('SQLD', '20010204', 'DB엔지니어링', TRUE),
    ('ADsP', '20010105', '빅데이터분석', TRUE),
    ('빅데이터분석기사', '20010105', '빅데이터분석', TRUE),
    ('전산회계', '02030201', '회계·감사', TRUE),
    ('웹디자인기능사', '08020104', '디지털디자인', TRUE),
    ('산업안전기사', '23060101', '산업안전관리', TRUE),
    ('조리기능사', '13010101', '한식조리', TRUE),
    ('공인중개사', '10020201', '부동산중개', TRUE),
    ('관광통역안내사', '12030103', '국내여행안내', TRUE);
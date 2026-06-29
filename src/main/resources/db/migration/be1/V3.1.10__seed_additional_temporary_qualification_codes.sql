INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '2290',
    '정보처리산업기사',
    'T',
    '국가기술자격',
    '01',
    '산업기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '2290'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '1325',
    '정보보안기사',
    'T',
    '국가기술자격',
    '01',
    '기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '1325'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '1324',
    '빅데이터분석기사',
    'T',
    '국가기술자격',
    '01',
    '기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '1324'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '2193',
    '사무자동화산업기사',
    'T',
    '국가기술자격',
    '01',
    '산업기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '2193'
);
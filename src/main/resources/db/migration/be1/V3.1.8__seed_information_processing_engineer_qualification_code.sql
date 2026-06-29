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
    '1320',
    '정보처리기사',
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
    WHERE jm_cd = '1320'
);
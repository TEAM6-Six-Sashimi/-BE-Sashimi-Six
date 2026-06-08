-- 기존 instructor_profiles의 단일 자격증 데이터를 새 테이블로 마이그레이션
INSERT INTO instructor_application_certifications (instructor_profile_id, certification_name, issued_by)
SELECT instructor_profile_id, certification_name, issued_by
FROM instructor_profiles
WHERE certification_name IS NOT NULL;

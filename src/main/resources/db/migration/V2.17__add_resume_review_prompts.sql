-- ============================================================
-- 0. Update ai_prompts purpose column
-- ============================================================

ALTER TABLE ai_prompts
    MODIFY COLUMN purpose VARCHAR(50) NOT NULL;


-- ============================================================
-- 1. AI resume review prompts
-- ============================================================

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '경력 업무 연속성 평가 프롬프트',
    'RESUME_CAREER_CONTINUITY',
    '다음 경력의 직무 흐름을 평가해주세요. 경력 목록: {careerHistory}',
    1,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_CAREER_CONTINUITY'
      AND version = 1
);

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '이력서 보완 피드백 프롬프트',
    'RESUME_IMPROVEMENT',
    '다음 평가 영역에 대한 짧은 보완 문장을 생성해주세요. 평가 영역: {sections}',
    1,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_IMPROVEMENT'
      AND version = 1
);


-- ============================================================
-- 2. Update resumes table
-- ============================================================

ALTER TABLE resumes
DROP COLUMN title,
    DROP COLUMN content,
    ADD COLUMN entry_level BOOLEAN NOT NULL DEFAULT TRUE;


-- ============================================================
-- 3. Resume educations
-- ============================================================

CREATE TABLE resume_educations (
                                   resume_education_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   resume_id BIGINT NOT NULL,

                                   school_name VARCHAR(255) NOT NULL,
                                   start_year_month CHAR(7) NOT NULL,
                                   end_year_month CHAR(7) NOT NULL,

                                   degree VARCHAR(30) NOT NULL,
                                   major VARCHAR(255) NOT NULL,
                                   graduation_status VARCHAR(30) NOT NULL,
                                   minor_or_research TEXT NULL,

                                   education_order INT NOT NULL,

                                   CONSTRAINT fk_resume_educations_resume
                                       FOREIGN KEY (resume_id)
                                           REFERENCES resumes(resume_id)
                                           ON DELETE CASCADE,

                                   INDEX idx_resume_education_order (
        resume_id,
        education_order
    )
);


-- ============================================================
-- 4. Resume careers
-- ============================================================

CREATE TABLE resume_careers (
                                resume_career_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                resume_id BIGINT NOT NULL,

                                company_name VARCHAR(255) NOT NULL,
                                start_year_month CHAR(7) NOT NULL,
                                end_year_month CHAR(7) NULL,

                                currently_employed BOOLEAN NOT NULL,
                                employment_type VARCHAR(30) NOT NULL,
                                custom_employment_type VARCHAR(255) NULL,
                                job_title VARCHAR(255) NOT NULL,

                                career_order INT NOT NULL,

                                CONSTRAINT fk_resume_careers_resume
                                    FOREIGN KEY (resume_id)
                                        REFERENCES resumes(resume_id)
                                        ON DELETE CASCADE,

                                INDEX idx_resume_career_order (
        resume_id,
        career_order
    )
);
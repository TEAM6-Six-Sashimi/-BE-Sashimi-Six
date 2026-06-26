DELETE r
FROM resumes r
JOIN (
    SELECT resume_id
    FROM (
        SELECT
            resume_id,
            ROW_NUMBER() OVER (
                PARTITION BY user_id
                ORDER BY COALESCE(updated_at, created_at) DESC, resume_id DESC
            ) AS rn
        FROM resumes
    ) ranked_resumes
    WHERE rn > 1
) duplicated_resumes
ON r.resume_id = duplicated_resumes.resume_id;

ALTER TABLE resumes
    ADD CONSTRAINT uq_resumes_user_id UNIQUE (user_id);
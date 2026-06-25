ALTER TABLE qualification_exam_schedules
DROP INDEX uq_qualification_exam_schedule;

ALTER TABLE qualification_exam_schedules
    MODIFY COLUMN qualification_name VARCHAR(255) NULL,
    MODIFY COLUMN jm_cd VARCHAR(20) NULL,
    MODIFY COLUMN qualification_type_code VARCHAR(10) NOT NULL,
    MODIFY COLUMN qualification_type_name VARCHAR(100) NOT NULL;

ALTER TABLE qualification_exam_schedules
    ADD COLUMN description VARCHAR(500) NULL AFTER qualification_type_name;

UPDATE qualification_exam_schedules
SET description = CONCAT(
        qualification_type_name,
        ' ',
        impl_year,
        '년도 제',
        impl_seq,
        '회'
                  )
WHERE description IS NULL;

ALTER TABLE qualification_exam_schedules
    MODIFY COLUMN description VARCHAR(500) NOT NULL;

DROP INDEX idx_qualification_exam_next
    ON qualification_exam_schedules;

CREATE INDEX idx_qualification_exam_schedule_lookup
    ON qualification_exam_schedules (
                                     impl_year,
                                     qualification_type_code,
                                     impl_seq
        );

CREATE INDEX idx_qualification_exam_schedule_exam_date
    ON qualification_exam_schedules (
                                     doc_exam_start_date
        );
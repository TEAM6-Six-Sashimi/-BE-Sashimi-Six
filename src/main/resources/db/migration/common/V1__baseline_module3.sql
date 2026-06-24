-- ============================================================
-- V1 baseline: module 3 final database state
-- This file folds the old V1~V10 migrations into one baseline.
-- Module 4 changes must start from V2.1.
-- ============================================================

-- =========================
-- 1. USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     login_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    interest_category_ids VARCHAR(255),
    phone VARCHAR(20),
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    status ENUM('ACTIVE', 'INACTIVE', 'DELETED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_image VARCHAR(500),
    referral_code VARCHAR(50) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL
    );

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uq_refresh_tokens_user UNIQUE (user_id)
    );

-- =========================
-- 2. CATEGORIES
-- =========================
CREATE TABLE IF NOT EXISTS categories (
                                          category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          main_category_id INT NOT NULL,
                                          name VARCHAR(100) NOT NULL,
    sub_category VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- =========================
-- 3. COURSES
-- =========================
CREATE TABLE IF NOT EXISTS courses (
                                       course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       title VARCHAR(255) NOT NULL,
    description TEXT,
    price BIGINT NOT NULL DEFAULT 0,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'BEGINNER',
    thumbnail VARCHAR(500),
    total_duration INT NOT NULL DEFAULT 0,
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED', 'CLOSED') NOT NULL DEFAULT 'DRAFT',
    reject_reason TEXT,
    rating_avg DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    review_count INT NOT NULL DEFAULT 0,
    student_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    approved_at DATETIME NULL,
    category_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    CONSTRAINT fk_courses_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_courses_instructor FOREIGN KEY (instructor_id) REFERENCES users(user_id)
    );

-- =========================
-- 4. COURSE_SESSIONS
-- =========================
CREATE TABLE IF NOT EXISTS course_sessions (
                                               session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               session_uid VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    video_url VARCHAR(500),
    duration_seconds INT NOT NULL DEFAULT 0,
    session_order INT NOT NULL,
    is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    attachment_name VARCHAR(255),
    attachment_url VARCHAR(500),
    attachment_type VARCHAR(100),
    attachment_size BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_sessions_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
    );

-- =========================
-- 5. CART_ITEMS
-- =========================
CREATE TABLE IF NOT EXISTS cart_items (
                                          cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          price BIGINT NOT NULL,
                                          selected BOOLEAN NOT NULL DEFAULT TRUE,
                                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          user_id BIGINT NOT NULL,
                                          course_id BIGINT NOT NULL,
                                          CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_cart_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT uq_cart_user_course UNIQUE (user_id, course_id)
    );

-- =========================
-- 6. ORDERS
-- =========================
CREATE TABLE IF NOT EXISTS orders (
                                      order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_no VARCHAR(100) NOT NULL UNIQUE,
    total_amount BIGINT NOT NULL DEFAULT 0,
    discount_amount BIGINT NOT NULL DEFAULT 0,
    final_amount BIGINT NOT NULL DEFAULT 0,
    status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 7. ORDER_ITEMS
-- =========================
CREATE TABLE IF NOT EXISTS order_items (
                                           order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           course_title VARCHAR(255) NOT NULL,
    price BIGINT NOT NULL,
    discount_amount BIGINT NOT NULL DEFAULT 0,
    final_price BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
    );

-- =========================
-- 8. PAYMENTS
-- =========================
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        amount BIGINT NOT NULL,
                                        status ENUM('READY', 'PAID', 'FAILED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'READY',
    paid_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uq_payment_order UNIQUE (order_id)
    );

-- =========================
-- 9. ENROLLMENTS
-- =========================
CREATE TABLE IF NOT EXISTS enrollments (
                                           enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           enrollment_type ENUM('PAID', 'FREE', 'SUBSCRIPTION') NOT NULL DEFAULT 'PAID',
    progress_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    order_item_id BIGINT NULL,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_enrollments_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(order_item_id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_enrollments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uq_enrollment_user_course UNIQUE (user_id, course_id)
    );

-- =========================
-- 10. LEARNING_PROGRESS
-- =========================
CREATE TABLE IF NOT EXISTS learning_progress (
                                                 progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 watched_seconds INT NOT NULL DEFAULT 0,
                                                 last_position_seconds INT NOT NULL DEFAULT 0,
                                                 progress_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    last_watched_at DATETIME NULL,
    session_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_progress_session FOREIGN KEY (session_id) REFERENCES course_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_progress_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uq_progress_user_session UNIQUE (user_id, session_id)
    );

-- =========================
-- 11. REVIEWS
-- =========================
CREATE TABLE IF NOT EXISTS reviews (
                                       review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       rating INT NOT NULL,
                                       content TEXT,
                                       status ENUM('ACTIVE', 'HIDDEN', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_reviews_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_review_user_course UNIQUE (user_id, course_id)
    );

-- =========================
-- 12. REVIEW_REPLIES
-- =========================
CREATE TABLE IF NOT EXISTS review_replies (
                                              reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              content TEXT NOT NULL,
                                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
                                              review_id BIGINT NOT NULL,
                                              user_id BIGINT NOT NULL,
                                              CONSTRAINT fk_review_replies_review FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_replies_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 13. COURSE_QNA
-- =========================
CREATE TABLE IF NOT EXISTS course_qna (
                                          qna_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('WAITING', 'ANSWERED', 'CLOSED') NOT NULL DEFAULT 'WAITING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    course_id BIGINT NOT NULL,
    session_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_qna_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_qna_session FOREIGN KEY (session_id) REFERENCES course_sessions(session_id),
    CONSTRAINT fk_qna_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 14. COURSE_ANNOUNCEMENTS
-- =========================
CREATE TABLE IF NOT EXISTS course_announcements (
                                                    announcement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_announcements_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT fk_announcements_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 15. COUPONS
-- =========================
CREATE TABLE IF NOT EXISTS coupons (
                                       coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    discount_type ENUM('AMOUNT', 'PERCENT') NOT NULL,
    discount_value BIGINT NOT NULL,
    min_order_amount BIGINT NOT NULL DEFAULT 0,
    started_at DATETIME NOT NULL,
    expired_at DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
    );

-- =========================
-- 16. COUPON_USAGES
-- =========================
CREATE TABLE IF NOT EXISTS coupon_usages (
                                             coupon_usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             discount_amount BIGINT NOT NULL,
                                             used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             user_id BIGINT NOT NULL,
                                             coupon_id BIGINT NOT NULL,
                                             order_id BIGINT NOT NULL,
                                             CONSTRAINT fk_coupon_usages_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_coupon_usages_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id),
    CONSTRAINT fk_coupon_usages_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT uq_coupon_order UNIQUE (coupon_id, order_id)
    );

-- =========================
-- 17. SUBSCRIPTIONS
-- =========================
CREATE TABLE IF NOT EXISTS subscriptions (
                                             subscription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             type ENUM('BASIC', 'PREMIUM', 'PRO') NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED', 'PAUSED') NOT NULL DEFAULT 'ACTIVE',
    price BIGINT NOT NULL,
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at DATETIME NULL,
    next_billing_at DATETIME NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 18. CREDITS
-- =========================
CREATE TABLE IF NOT EXISTS credits (
                                       credit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       balance BIGINT NOT NULL DEFAULT 0,
                                       updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
                                       user_id BIGINT NOT NULL UNIQUE,
                                       CONSTRAINT fk_credits_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 19. INSTRUCTOR_PROFILES
-- =========================
CREATE TABLE IF NOT EXISTS instructor_profiles (
                                                   instructor_profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   bio TEXT,
                                                   portfolio_url VARCHAR(500),
    certification_name VARCHAR(255),
    issued_by VARCHAR(255),
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    approved_at DATETIME NULL,
    created_at DATETIME DEFAULT NOW(),
    updated_at DATETIME DEFAULT NOW() ON UPDATE NOW(),
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_instructor_profiles_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS instructor_application_certifications (
                                                                     certification_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                                     instructor_profile_id  BIGINT NOT NULL,
                                                                     certification_name     VARCHAR(255) NOT NULL,
    issued_by              VARCHAR(255),
    CONSTRAINT fk_instr_cert_profile FOREIGN KEY (instructor_profile_id)
    REFERENCES instructor_profiles(instructor_profile_id) ON DELETE CASCADE
    );

-- =========================
-- 20. INSTRUCTOR_BANK_ACCOUNTS
-- =========================
CREATE TABLE IF NOT EXISTS instructor_bank_accounts (
                                                        bank_account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                        bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    account_holder VARCHAR(100) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_bank_accounts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 21. SETTLEMENTS
-- =========================
CREATE TABLE IF NOT EXISTS settlements (
                                           settlement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           period_start DATE NOT NULL,
                                           period_end DATE NOT NULL,
                                           total_sales BIGINT NOT NULL DEFAULT 0,
                                           commission_amount BIGINT NOT NULL DEFAULT 0,
                                           settlement_amount BIGINT NOT NULL DEFAULT 0,
                                           status ENUM('PENDING', 'APPROVED', 'PAID', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    paid_at DATETIME NULL,
    approved_by BIGINT NULL,
    user_id BIGINT NOT NULL,
    bank_account_id BIGINT NOT NULL,
    CONSTRAINT fk_settlements_instructor FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_settlements_bank FOREIGN KEY (bank_account_id) REFERENCES instructor_bank_accounts(bank_account_id),
    CONSTRAINT fk_settlements_approved_by FOREIGN KEY (approved_by) REFERENCES users(user_id)
    );

-- =========================
-- 22. NOTIFICATION_SETTINGS
-- =========================
CREATE TABLE IF NOT EXISTS notification_settings (
                                                     setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     qna_notification BOOLEAN NOT NULL DEFAULT TRUE,
                                                     payment_notification BOOLEAN NOT NULL DEFAULT TRUE,
                                                     marketing_notification BOOLEAN NOT NULL DEFAULT FALSE,
                                                     email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                     push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                     updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
                                                     user_id BIGINT NOT NULL UNIQUE,
                                                     CONSTRAINT fk_notification_settings_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 23. NOTIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             type ENUM('QNA', 'PAYMENT', 'COURSE', 'SYSTEM', 'MARKETING') NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    link_url VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 24. EMAIL_VERIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS email_verifications (
                                                   verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   target_email VARCHAR(255) NOT NULL,
    code VARCHAR(20) NOT NULL,
    purpose ENUM('SIGNUP', 'PASSWORD_RESET', 'EMAIL_CHANGE') NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    expired_at DATETIME NOT NULL,
    verified_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NULL,
    CONSTRAINT fk_email_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 25. AI_PROMPTS
-- =========================
CREATE TABLE IF NOT EXISTS ai_prompts (
                                          prompt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    purpose ENUM('RESUME_GENERATION', 'RESUME_EVALUATION', 'JOB_ANALYSIS', 'COURSE_RECOMMENDATION') NOT NULL,
    prompt TEXT NOT NULL,
    version INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP
    );

-- =========================
-- 26. RESUMES
-- =========================
CREATE TABLE IF NOT EXISTS resumes (
                                       resume_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       title VARCHAR(255) NOT NULL,
    content JSON,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 27. JOB_POSTINGS
-- =========================
CREATE TABLE IF NOT EXISTS job_postings (
                                            job_posting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            source_url VARCHAR(500),
    company_name VARCHAR(100) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    raw_content TEXT,
    ai_summary TEXT,
    analysis_status ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    analysis_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    prompt_id BIGINT NOT NULL,
    CONSTRAINT fk_job_postings_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_job_postings_prompt FOREIGN KEY (prompt_id) REFERENCES ai_prompts(prompt_id)
    );

-- =========================
-- 28. SKILLS
-- =========================
CREATE TABLE IF NOT EXISTS skills (
                                      skill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100),
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- =========================
-- 29. JOB_REQUIRED_SKILLS
-- =========================
CREATE TABLE IF NOT EXISTS job_required_skills (
                                                   required_skill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   importance ENUM('LOW', 'MEDIUM', 'HIGH', 'REQUIRED') NOT NULL DEFAULT 'MEDIUM',
    required_level VARCHAR(100),
    extracted_text TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    job_posting_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    CONSTRAINT fk_required_skills_job FOREIGN KEY (job_posting_id) REFERENCES job_postings(job_posting_id) ON DELETE CASCADE,
    CONSTRAINT fk_required_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id),
    CONSTRAINT uq_job_skill UNIQUE (job_posting_id, skill_id)
    );

-- =========================
-- 30. COURSE_SKILLS
-- =========================
CREATE TABLE IF NOT EXISTS course_skills (
                                             course_skill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             skill_level ENUM('BASIC', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'BASIC',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    skill_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_course_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id),
    CONSTRAINT fk_course_skills_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT uq_course_skill UNIQUE (course_id, skill_id)
    );

-- =========================
-- 31. AI_RESUME_EVALUATIONS
-- =========================
CREATE TABLE IF NOT EXISTS ai_resume_evaluations (
                                                     evaluation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     overall_score DECIMAL(5,2),
    strengths TEXT,
    weaknesses TEXT,
    suggestions TEXT,
    ai_result JSON,
    evaluated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resume_id BIGINT NOT NULL,
    job_posting_id BIGINT NULL,
    prompt_id BIGINT NOT NULL,
    CONSTRAINT fk_resume_evaluations_resume FOREIGN KEY (resume_id) REFERENCES resumes(resume_id) ON DELETE CASCADE,
    CONSTRAINT fk_resume_evaluations_job FOREIGN KEY (job_posting_id) REFERENCES job_postings(job_posting_id),
    CONSTRAINT fk_resume_evaluations_prompt FOREIGN KEY (prompt_id) REFERENCES ai_prompts(prompt_id)
    );

-- =========================
-- 32. AI_RECOMMENDATIONS
-- =========================
CREATE TABLE IF NOT EXISTS ai_recommendations (
                                                  recommendation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  source_type ENUM('RESUME', 'JOB_POSTING', 'COURSE', 'USER') NOT NULL,
    source_id BIGINT NULL,
    recommended_type ENUM('COURSE', 'CERTIFICATION', 'SKILL', 'JOB_POSTING') NOT NULL,
    recommended_id BIGINT NULL,
    score DECIMAL(5,2),
    reason TEXT,
    ai_result JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prompt_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_recommendations_prompt FOREIGN KEY (prompt_id) REFERENCES ai_prompts(prompt_id),
    CONSTRAINT fk_recommendations_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 33. CERTIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS certifications (
                                              certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              name VARCHAR(100) NOT NULL,
    description TEXT,
    level VARCHAR(50),
    issuing_organization VARCHAR(100),
    pass_rate DECIMAL(5,2),
    employment_rate DECIMAL(5,2),
    external_url VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_certifications_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
    );

-- =========================
-- 34. EXTERNAL_EXAM_LINKS
-- =========================
CREATE TABLE IF NOT EXISTS external_exam_links (
                                                   exam_link_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   title VARCHAR(255) NOT NULL,
    provider_name VARCHAR(100),
    external_url VARCHAR(500) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    certification_id BIGINT NOT NULL,
    CONSTRAINT fk_exam_links_certification FOREIGN KEY (certification_id) REFERENCES certifications(certification_id) ON DELETE CASCADE
    );

-- =========================
-- 35. ROADMAPS
-- =========================
CREATE TABLE IF NOT EXISTS roadmaps (
                                        roadmap_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        goal_type ENUM('CERTIFICATION', 'JOB', 'COURSE') NOT NULL,
    title VARCHAR(255) NOT NULL,
    target_name VARCHAR(100),
    due_date DATE,
    created_by_ai BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('ACTIVE', 'COMPLETED', 'PAUSED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_roadmaps_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 36. ROADMAP_STEPS
-- =========================
CREATE TABLE IF NOT EXISTS roadmap_steps (
                                             step_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             title VARCHAR(255) NOT NULL,
    description TEXT,
    step_order INT NOT NULL,
    status ENUM('TODO', 'IN_PROGRESS', 'DONE', 'SKIPPED') NOT NULL DEFAULT 'TODO',
    course_id BIGINT NULL,
    roadmap_id BIGINT NOT NULL,
    certification_id BIGINT NULL,
    CONSTRAINT fk_roadmap_steps_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_roadmap_steps_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps(roadmap_id) ON DELETE CASCADE,
    CONSTRAINT fk_roadmap_steps_certification FOREIGN KEY (certification_id) REFERENCES certifications(certification_id)
    );

-- =========================
-- 37. INQUIRIES
-- =========================
CREATE TABLE IF NOT EXISTS inquiries (
                                         inquiry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         category VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('WAITING', 'ANSWERED', 'CLOSED') NOT NULL DEFAULT 'WAITING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_inquiries_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 38. INQUIRY_REPLIES
-- =========================
CREATE TABLE IF NOT EXISTS inquiry_replies (
                                               reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               content TEXT NOT NULL,
                                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               inquiry_id BIGINT NOT NULL,
                                               user_id BIGINT NOT NULL,
                                               CONSTRAINT fk_inquiry_replies_inquiry FOREIGN KEY (inquiry_id) REFERENCES inquiries(inquiry_id) ON DELETE CASCADE,
    CONSTRAINT fk_inquiry_replies_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 39. SYSTEM_NOTICES
-- =========================
CREATE TABLE IF NOT EXISTS system_notices (
                                              notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('DRAFT', 'PUBLISHED', 'HIDDEN') NOT NULL DEFAULT 'PUBLISHED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_system_notices_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 40. BOOKMARKS
-- =========================
CREATE TABLE IF NOT EXISTS bookmarks (
                                         bookmark_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         timestamp_seconds INT NOT NULL DEFAULT 0,
                                         memo TEXT,
                                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         user_id BIGINT NOT NULL,
                                         session_id BIGINT NOT NULL,
                                         CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_bookmarks_session FOREIGN KEY (session_id) REFERENCES course_sessions(session_id) ON DELETE CASCADE
    );

-- =========================
-- 41. LECTURE_NOTES
-- =========================
CREATE TABLE IF NOT EXISTS lecture_notes (
                                             note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             title VARCHAR(255),
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_lecture_notes_session FOREIGN KEY (session_id) REFERENCES course_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_lecture_notes_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 42. REPORTS
-- =========================
CREATE TABLE IF NOT EXISTS reports (
                                       report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       target_type ENUM('REVIEW', 'QNA', 'COURSE', 'USER') NOT NULL,
    target_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    processed_at DATETIME NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 43. ADMIN_LOGS
-- =========================
CREATE TABLE IF NOT EXISTS admin_logs (
                                          log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          action_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id BIGINT,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_admin_logs_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 44. USER_ACTIVITY_LOGS
-- =========================
CREATE TABLE IF NOT EXISTS user_activity_logs (
                                                  activity_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  action_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id BIGINT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_activity_logs_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 45. D_DAYS
-- =========================
CREATE TABLE IF NOT EXISTS d_days (
                                      d_day_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      title VARCHAR(255) NOT NULL,
    target_date DATE NOT NULL,
    category ENUM('CERTIFICATION', 'JOB', 'COURSE', 'PERSONAL') NOT NULL DEFAULT 'PERSONAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_d_days_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- =========================
-- 46. USER_CERTIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS user_certifications (
                                                   user_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   user_id               BIGINT NOT NULL,
                                                   certification_name    VARCHAR(255) NOT NULL,
    issued_by             VARCHAR(255),
    issued_date           DATE,
    file_name             VARCHAR(500),
    status                ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    created_at            DATETIME DEFAULT NOW(),
    deleted_at            DATETIME NULL,
    CONSTRAINT fk_user_cert_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- ============================================================
-- Seed data folded from module 3 migrations
-- ============================================================

-- Base users from old V2

INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin01', 'admin@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자', '1985-01-01', '010-0000-0000', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN001'),
    ('instructor01', 'instructor1@test.com', '$2a$10$SwKCvl1vtkjOSh0F.KflsOx8eolg1MTZdqUXcfzZQ4piogm8.PosK', '김강사', '1988-03-12', '010-1111-1111', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS001'),
    ('instructor02', 'instructor2@test.com', '$2a$10$SwKCvl1vtkjOSh0F.KflsOx8eolg1MTZdqUXcfzZQ4piogm8.PosK', '이강사', '1990-07-20', '010-2222-2222', 'INSTRUCTOR', 'ACTIVE', TRUE, 'INS002'),
    ('student01', 'student1@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '박학생', '2001-05-14', '010-3333-3333', 'STUDENT', 'ACTIVE', TRUE, 'STD001'),
    ('student02', 'student2@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '최학생', '2000-10-03', '010-4444-4444', 'STUDENT', 'ACTIVE', TRUE, 'STD002'),
    ('student03', 'student3@test.com', '$2a$10$vYwWcwkU/lHpqmL3WERfgOi0yx/OxCkfbtGQHJjIj8IpfTdGptORi', '정학생', '1999-12-25', '010-5555-5555', 'STUDENT', 'ACTIVE', TRUE, 'STD003');

-- Final categories from old V9

INSERT IGNORE INTO categories (main_category_id, name, sub_category, sort_order, is_active)
VALUES
    (1, 'IT·정보통신', '정보처리기사', 1, TRUE),
    (1, 'IT·정보통신', '정보보안기사', 2, TRUE),
    (1, 'IT·정보통신', '네트워크관리사', 3, TRUE),
    (1, 'IT·정보통신', '빅데이터분석기사', 4, TRUE),
    (1, 'IT·정보통신', 'ADsP', 5, TRUE),
    (1, 'IT·정보통신', 'SQLD', 6, TRUE),
    (2, '경영·회계', '전산회계', 7, TRUE),
    (2, '경영·회계', '재경관리사', 8, TRUE),
    (2, '경영·회계', '물류관리사', 9, TRUE),
    (2, '경영·회계', '사회조사분석사', 10, TRUE),
    (2, '경영·회계', '유통관리사', 11, TRUE),
    (3, '디자인', '컴퓨터그래픽스운용기능사', 12, TRUE),
    (3, '디자인', '시각디자인산업기사', 13, TRUE),
    (3, '디자인', '웹디자인기능사', 14, TRUE),
    (3, '디자인', '실내건축기능사', 15, TRUE),
    (4, '건설·안전', '산업안전기사', 16, TRUE),
    (4, '건설·안전', '건설안전기사', 17, TRUE),
    (4, '건설·안전', '소방설비기사', 18, TRUE),
    (4, '건설·안전', '위험물산업기사', 19, TRUE),
    (5, '식품·조리', '조리기능사', 20, TRUE),
    (5, '식품·조리', '제과기능사', 21, TRUE),
    (5, '식품·조리', '제빵기능사', 22, TRUE),
    (5, '식품·조리', '식품기사', 23, TRUE),
    (5, '식품·조리', '영양사', 24, TRUE),
    (6, '부동산·금융', '공인중개사', 25, TRUE),
    (6, '부동산·금융', '주택관리사', 26, TRUE),
    (6, '부동산·금융', '감정평가사', 27, TRUE),
    (7, '어학', '한국어교원자격증', 28, TRUE),
    (7, '어학', '관광통역안내사', 29, TRUE);

-- Final courses from old V10

INSERT INTO courses
(title, description, price, difficulty, thumbnail, total_duration, status, reject_reason, rating_avg, review_count, student_count, created_at, approved_at, category_id, instructor_id)
VALUES
    -- IT·정보통신
    ('정보처리기사 필기 완전정복', '최신 출제기준에 맞춘 정보처리기사 필기 핵심 이론과 기출문제 풀이 강의입니다.', 55000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 480, 'APPROVED', NULL, 4.80, 312, 2100, '2025-01-05 09:00:00', '2025-01-07 10:00:00', 1, 2),
    ('정보처리기사 실기 단기합격', '실기 시험에 자주 나오는 핵심 알고리즘과 SQL을 집중적으로 학습합니다.', 65000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 560, 'APPROVED', NULL, 4.75, 278, 1850, '2025-01-08 09:00:00', '2025-01-10 10:00:00', 1, 3),
    ('정보보안기사 합격 로드맵', '보안 이론부터 실습까지, 정보보안기사 자격증 취득을 위한 체계적인 커리큘럼입니다.', 70000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 620, 'APPROVED', NULL, 4.82, 195, 980, '2025-01-12 09:00:00', '2025-01-14 10:00:00', 2, 2),
    ('네트워크관리사 2급 한번에 끝내기', '네트워크 기초 개념부터 실전 문제까지 네트워크관리사 2급 시험 대비 완성 강의입니다.', 45000, 'BEGINNER', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 390, 'APPROVED', NULL, 4.70, 143, 760, '2025-01-15 09:00:00', '2025-01-17 10:00:00', 3, 3),
    ('빅데이터분석기사 필기+실기 패키지', 'R과 Python을 활용한 빅데이터 분석 실무 중심의 자격증 취득 강의입니다.', 80000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 700, 'APPROVED', NULL, 4.88, 224, 1340, '2025-01-18 09:00:00', '2025-01-20 10:00:00', 4, 2),
    ('ADsP 데이터분석 준전문가 단기완성', 'ADsP 시험의 핵심 개념과 기출 유형을 빠르게 정리하는 단기 합격 전략 강의입니다.', 40000, 'BEGINNER', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 300, 'APPROVED', NULL, 4.72, 168, 920, '2025-01-20 09:00:00', '2025-01-22 10:00:00', 5, 3),
    ('SQLD 자격증 SQL 기초부터 합격까지', 'SQL 기본 문법부터 SQLD 시험 핵심 포인트까지 한 번에 정리하는 강의입니다.', 42000, 'BEGINNER', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 350, 'APPROVED', NULL, 4.78, 201, 1150, '2025-01-22 09:00:00', '2025-01-24 10:00:00', 6, 2),

    -- 경영·회계
    ('전산회계 1급 실전 완성', 'KcLep 프로그램 실습과 함께 전산회계 1급 이론을 완벽하게 정리하는 강의입니다.', 48000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 420, 'APPROVED', NULL, 4.76, 185, 1020, '2025-02-01 09:00:00', '2025-02-03 10:00:00', 7, 3),
    ('전산회계 2급 초보자 입문', '회계 지식이 전혀 없어도 시작할 수 있는 전산회계 2급 입문 강의입니다.', 35000, 'BEGINNER', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 280, 'APPROVED', NULL, 4.65, 142, 880, '2025-02-03 09:00:00', '2025-02-05 10:00:00', 7, 2),
    ('재경관리사 재무회계·세무·원가 통합반', '재무회계, 세무회계, 원가관리회계를 한 번에 준비하는 재경관리사 합격 강의입니다.', 75000, 'ADVANCED', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 680, 'APPROVED', NULL, 4.83, 167, 740, '2025-02-05 09:00:00', '2025-02-07 10:00:00', 8, 3),
    ('물류관리사 한번에 합격하기', '물류 관련 법규와 실무를 체계적으로 정리한 물류관리사 자격증 대비 강의입니다.', 50000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 440, 'APPROVED', NULL, 4.68, 123, 610, '2025-02-08 09:00:00', '2025-02-10 10:00:00', 9, 2),
    ('사회조사분석사 2급 핵심정리', '설문 설계부터 데이터 분석까지, 사회조사분석사 2급 합격을 위한 핵심 강의입니다.', 45000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 380, 'APPROVED', NULL, 4.71, 98, 520, '2025-02-10 09:00:00', '2025-02-12 10:00:00', 10, 3),
    ('유통관리사 2급 단기 합격전략', '유통 산업의 핵심 이론과 최신 기출문제를 바탕으로 단기 합격을 목표로 하는 강의입니다.', 42000, 'BEGINNER', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 320, 'APPROVED', NULL, 4.66, 115, 670, '2025-02-12 09:00:00', '2025-02-14 10:00:00', 11, 2),

    -- 디자인
    ('컴퓨터그래픽스운용기능사 실기 완성', '포토샵·일러스트레이터를 활용한 컴퓨터그래픽스운용기능사 실기 합격 강의입니다.', 55000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 500, 'APPROVED', NULL, 4.79, 178, 890, '2025-03-01 09:00:00', '2025-03-03 10:00:00', 12, 3),
    ('시각디자인산업기사 이론+실기 패키지', '시각디자인의 이론 기초부터 실기 작품 제작까지 완성하는 종합 강의입니다.', 65000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 580, 'APPROVED', NULL, 4.74, 134, 620, '2025-03-05 09:00:00', '2025-03-07 10:00:00', 13, 2),
    ('웹디자인기능사 HTML·CSS 실전', 'HTML, CSS, 자바스크립트를 활용한 웹디자인기능사 실기 완벽 대비 강의입니다.', 50000, 'BEGINNER', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 420, 'APPROVED', NULL, 4.81, 209, 1100, '2025-03-08 09:00:00', '2025-03-10 10:00:00', 14, 3),
    ('실내건축기능사 도면 실기 완성', '실내 공간 설계와 도면 작성법을 중심으로 실내건축기능사 합격을 준비하는 강의입니다.', 58000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 460, 'APPROVED', NULL, 4.69, 112, 530, '2025-03-12 09:00:00', '2025-03-14 10:00:00', 15, 2),

    -- 건설·안전
    ('산업안전기사 필기 핵심 요약', '산업안전 법규와 기준을 빠르게 정리하고 기출 문제로 실전 감각을 키우는 강의입니다.', 52000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 430, 'APPROVED', NULL, 4.77, 156, 840, '2025-04-01 09:00:00', '2025-04-03 10:00:00', 16, 3),
    ('건설안전기사 이론 완전정복', '건설현장 안전관리 이론과 관계 법령을 체계적으로 학습하는 강의입니다.', 55000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 470, 'APPROVED', NULL, 4.72, 132, 680, '2025-04-05 09:00:00', '2025-04-07 10:00:00', 17, 2),
    ('소방설비기사 전기·기계 통합반', '소방설비기사 전기분야와 기계분야를 함께 준비하는 통합 합격 전략 강의입니다.', 70000, 'ADVANCED', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 640, 'APPROVED', NULL, 4.80, 148, 720, '2025-04-08 09:00:00', '2025-04-10 10:00:00', 18, 3),
    ('위험물산업기사 단기 합격 전략', '위험물 관련 법규와 화학 이론을 집중 정리한 위험물산업기사 대비 강의입니다.', 45000, 'INTERMEDIATE', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 360, 'APPROVED', NULL, 4.67, 104, 510, '2025-04-12 09:00:00', '2025-04-14 10:00:00', 19, 2),

    -- 식품·조리
    ('한식조리기능사 실기 완성', '한식 조리 실기 시험의 20가지 과제를 하나씩 완벽하게 정복하는 강의입니다.', 48000, 'BEGINNER', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 400, 'APPROVED', NULL, 4.85, 267, 1580, '2025-05-01 09:00:00', '2025-05-03 10:00:00', 20, 3),
    ('양식조리기능사 실기 마스터', '양식 조리 실기 과제 전 메뉴를 반복 실습하며 합격 실력을 키우는 강의입니다.', 48000, 'BEGINNER', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 380, 'APPROVED', NULL, 4.78, 198, 1120, '2025-05-03 09:00:00', '2025-05-05 10:00:00', 20, 2),
    ('제과기능사 실기 완벽 대비', '제과 실기 시험 과제별 레시피와 핵심 포인트를 집중 정리한 합격 강의입니다.', 50000, 'BEGINNER', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 410, 'APPROVED', NULL, 4.82, 215, 1230, '2025-05-05 09:00:00', '2025-05-07 10:00:00', 21, 3),
    ('제빵기능사 실기 한번에 합격', '빵 반죽부터 완성까지, 제빵기능사 실기 전 과제를 체계적으로 학습하는 강의입니다.', 50000, 'BEGINNER', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 410, 'APPROVED', NULL, 4.80, 187, 1050, '2025-05-08 09:00:00', '2025-05-10 10:00:00', 22, 2),
    ('식품기사 필기 핵심이론 총정리', '식품화학, 식품위생, 식품가공 등 필기 핵심 이론을 체계적으로 정리하는 강의입니다.', 58000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 490, 'APPROVED', NULL, 4.73, 143, 710, '2025-05-12 09:00:00', '2025-05-14 10:00:00', 23, 3),
    ('영양사 국가시험 합격 전략', '영양사 국가시험 전 과목을 체계적으로 정리하고 기출문제로 실전 대비하는 강의입니다.', 75000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 660, 'APPROVED', NULL, 4.84, 176, 820, '2025-05-15 09:00:00', '2025-05-17 10:00:00', 24, 2),

    -- 부동산·금융
    ('공인중개사 1차 민법 완전정복', '공인중개사 1차 시험의 핵심 과목인 민법 및 민사특별법을 집중 학습하는 강의입니다.', 80000, 'ADVANCED', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 720, 'APPROVED', NULL, 4.86, 345, 2300, '2025-06-01 09:00:00', '2025-06-03 10:00:00', 25, 3),
    ('공인중개사 2차 부동산공법 완성', '공인중개사 2차 시험 부동산공법과 중개사법을 핵심 위주로 정리하는 강의입니다.', 80000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 700, 'APPROVED', NULL, 4.83, 312, 2100, '2025-06-03 09:00:00', '2025-06-05 10:00:00', 25, 2),
    ('주택관리사 1차 회계원리·시설개론', '주택관리사 1차 시험 핵심 과목을 한 번에 정리하는 단기 합격 강의입니다.', 72000, 'ADVANCED', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 650, 'APPROVED', NULL, 4.79, 198, 1050, '2025-06-08 09:00:00', '2025-06-10 10:00:00', 26, 3),
    ('감정평가사 1차 경제학원론 기초', '감정평가사 1차 시험 경제학원론을 기초부터 심화까지 완벽하게 준비하는 강의입니다.', 90000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 780, 'APPROVED', NULL, 4.87, 134, 580, '2025-06-12 09:00:00', '2025-06-14 10:00:00', 27, 2),

    -- 어학
    ('한국어교원자격증 2급 취득 완성', '외국어로서의 한국어 교육 이론과 실습을 통해 한국어교원자격증 2급을 취득하는 강의입니다.', 68000, 'INTERMEDIATE', 'http://localhost:8080/uploads/79b2e733-258c-4b3a-b7a1-2d571948edfa.png', 580, 'APPROVED', NULL, 4.81, 167, 780, '2025-07-01 09:00:00', '2025-07-03 10:00:00', 28, 3),
    ('관광통역안내사 영어 필기+면접 완성', '관광법규, 관광학개론부터 영어 면접까지 관광통역안내사 합격의 모든 것을 담은 강의입니다.', 75000, 'ADVANCED', 'http://localhost:8080/uploads/4fc10d48-d560-4ee0-9a36-8fd88daa6d9e.png', 640, 'APPROVED', NULL, 4.83, 145, 670, '2025-07-05 09:00:00', '2025-07-07 10:00:00', 29, 2);

-- AI prompts from old V2

INSERT IGNORE INTO ai_prompts
(name, purpose, prompt, version, is_active)
VALUES
    ('이력서 생성 프롬프트 v1', 'RESUME_GENERATION',
     '사용자의 경력과 기술스택을 바탕으로 신입 개발자 이력서 초안을 작성해주세요. 사용자 정보: {userProfile}',
     1, TRUE),
    ('이력서 평가 프롬프트 v1', 'RESUME_EVALUATION',
     '아래 이력서 내용을 BASIC(기본 정보), EDUCATION(학력 사항), CAREER(경력 사항), SKILL(기술 및 자격증) 기준으로 평가해주세요. 이력서 제목: {resumeTitle}, 이력서 내용: {resumeContent}',
     1, TRUE),
    ('채용공고 분석 프롬프트 v1', 'JOB_ANALYSIS',
     '아래 채용공고 내용을 분석해서 직무명, 요구 역량, 추천 자격증, 보완 학습이 필요한 강의 방향을 도출해주세요. 이력서 보유 여부: {resumeBased}, 채용공고 내용: {jobPostingContent}',
     1, TRUE),
    ('강의 추천 프롬프트 v1', 'COURSE_RECOMMENDATION',
     '사용자의 목표, 부족한 기술, 채용공고 요구 역량을 바탕으로 적합한 강의를 추천해주세요. 목표: {targetGoal}, 부족한 기술: {missingSkills}, 요구 역량: {requiredSkills}',
     1, TRUE);

-- Approved instructor profiles from old V2

INSERT IGNORE INTO instructor_profiles
(bio, portfolio_url, certification_name, issued_by, approval_status, approved_at, user_id)
VALUES
    ('정보처리기사와 SQLD 자격증 강의를 전문으로 합니다.', 'https://portfolio.example.com/instructor1', '정보처리기사', '한국산업인력공단', 'APPROVED', NOW(), 2),
    ('데이터 분석 자격증과 빅데이터분석기사 강의를 진행합니다.', 'https://portfolio.example.com/instructor2', 'ADsP', '한국데이터산업진흥원', 'APPROVED', NOW(), 3);

-- Initial credits from old V2

INSERT IGNORE INTO credits
(balance, user_id)
VALUES
    (15000, 4),
    (5000, 5),
    (0, 6);

-- Admin password finalization from old V3

UPDATE users
SET password = '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C'
WHERE login_id = 'admin01';

-- Pending instructor application seed data from old V5

-- 강사 신청 대기(PENDING) 더미 데이터 추가
-- 신규 유저 3명 추가 (강사 신청 예정자)
INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('pending01', 'pending1@test.com', '$2a$10$pending', '홍길동', '1995-04-11', '010-6666-6666', 'STUDENT', 'ACTIVE', TRUE, 'PND001'),
    ('pending02', 'pending2@test.com', '$2a$10$pending', '이순신', '1993-08-22', '010-7777-7777', 'STUDENT', 'ACTIVE', TRUE, 'PND002'),
    ('pending03', 'pending3@test.com', '$2a$10$pending', '강감찬', '1997-02-05', '010-8888-8888', 'STUDENT', 'ACTIVE', TRUE, 'PND003');

-- 강사 신청 PENDING 데이터 추가
INSERT IGNORE INTO instructor_profiles
(bio, portfolio_url, certification_name, issued_by, approval_status, approved_at, user_id)
VALUES
    ('정보처리기사 자격증 보유. 백엔드 개발 7년 경력으로 실무 중심 강의를 진행합니다.',
     'https://portfolio.example.com/pending1',
     '정보처리기사', '한국산업인력공단',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending01')),

    ('SQLD, 빅데이터분석기사 보유. 데이터 분석 분야 강의 전문입니다.',
     'https://portfolio.example.com/pending2',
     '빅데이터분석기사', '한국데이터산업진흥원',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending02')),

    ('한국사능력검정시험 1급 보유. 역사 관련 강의를 희망합니다.',
     'https://portfolio.example.com/pending3',
     '한국사능력검정시험', '국사편찬위원회',
     'PENDING', NULL,
     (SELECT user_id FROM users WHERE login_id = 'pending03'));

-- Dummy order/payment/enrollment/notification data from old V6, excluding overwritten courses and course sessions

INSERT IGNORE INTO orders
(order_no, total_amount, discount_amount, final_amount, status, user_id)
VALUES
    ('ORD-20260521-0001', 108000, 10000, 98000, 'PAID', 4),
    ('ORD-20260521-0002', 59000, 0, 59000, 'PAID', 5),
    ('ORD-20260521-0003', 39000, 0, 39000, 'PENDING', 6);

INSERT IGNORE INTO order_items
(course_title, price, discount_amount, final_price, order_id, course_id)
VALUES
    ('ChatGPT로 업무 자동화 완성하기', 49000, 5000, 44000, 1, 1),
    ('프롬프트 엔지니어링 마스터 클래스', 59000, 5000, 54000, 1, 2),
    ('Midjourney로 AI 이미지 생성 입문', 39000, 0, 39000, 2, 3),
    ('ChatGPT로 업무 자동화 완성하기', 49000, 0, 49000, 3, 1);

INSERT IGNORE INTO payments
(amount, status, paid_at, order_id, user_id)
VALUES
    (98000, 'PAID', NOW(), 1, 4),
    (39000, 'PAID', NOW(), 2, 5),
    (39000, 'READY', NULL, 3, 6);

INSERT IGNORE INTO enrollments
(enrollment_type, progress_rate, is_completed, enrolled_at, completed_at, order_item_id, course_id, user_id)
VALUES
    ('PAID', 66.67, FALSE, NOW(), NULL, 1, 1, 4),
    ('PAID', 20.00, FALSE, NOW(), NULL, 2, 2, 4),
    ('PAID', 100.00, TRUE, NOW(), NOW(), 3, 3, 5);

INSERT IGNORE INTO notification_settings
(qna_notification, payment_notification, marketing_notification, email_enabled, push_enabled, user_id)
VALUES
    (TRUE, TRUE, FALSE, TRUE, TRUE, 4),
    (TRUE, TRUE, TRUE, TRUE, TRUE, 5),
    (TRUE, FALSE, FALSE, TRUE, FALSE, 6),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 2),
    (TRUE, TRUE, FALSE, TRUE, TRUE, 3);

-- Instructor certification data migration from old V8

-- 기존 instructor_profiles의 단일 자격증 데이터를 새 테이블로 마이그레이션
INSERT INTO instructor_application_certifications (instructor_profile_id, certification_name, issued_by)
SELECT instructor_profile_id, certification_name, issued_by
FROM instructor_profiles
WHERE certification_name IS NOT NULL;


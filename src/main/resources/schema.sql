DROP DATABASE IF EXISTS sixsashimi_db;
CREATE DATABASE sixsashimi_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sixsashimi_db;

-- =========================
-- 1. USERS
-- =========================
CREATE TABLE users (
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

CREATE TABLE refresh_tokens (
                                refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token VARCHAR(512) NOT NULL UNIQUE,
                                expiry_date DATETIME NOT NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(user_id),

                                CONSTRAINT uq_refresh_tokens_user
                                    UNIQUE (user_id)
);

-- =========================
-- 2. CATEGORIES
-- =========================
CREATE TABLE categories (
                            category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            main_category_id INT NOT NULL,
                            name ENUM('ai·데이터', '건강·자격증', '라이프·교육', '마케팅·비즈니스', '외국어', '재테크·투자', '취미·문화') NOT NULL,
                            sub_category VARCHAR(100) NOT NULL,
                            sort_order INT NOT NULL DEFAULT 0,
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 3. COURSES
-- =========================
CREATE TABLE courses (
                         course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL DEFAULT 0,
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
CREATE TABLE course_sessions (
                                 session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 session_uid      VARCHAR(36)     NOT NULL,
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
CREATE TABLE cart_items (
                            cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            price DECIMAL(10,2) NOT NULL,
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
CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_no VARCHAR(100) NOT NULL UNIQUE,
                        total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                        discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                        final_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                        status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        user_id BIGINT NOT NULL,
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- 7. ORDER_ITEMS
-- =========================
CREATE TABLE order_items (
                             order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             course_title VARCHAR(255) NOT NULL,
                             price DECIMAL(10,2) NOT NULL,
                             discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                             final_price DECIMAL(10,2) NOT NULL,
                             order_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                             CONSTRAINT fk_order_items_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- =========================
-- 8. PAYMENTS
-- =========================
CREATE TABLE payments (
                          payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          amount DECIMAL(10,2) NOT NULL,
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
CREATE TABLE enrollments (
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
CREATE TABLE learning_progress (
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
CREATE TABLE reviews (
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
CREATE TABLE review_replies (
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
CREATE TABLE course_qna (
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
CREATE TABLE course_announcements (
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

CREATE TABLE coupons (
                         coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(100) NOT NULL UNIQUE,
                         name VARCHAR(100) NOT NULL,
                         discount_type ENUM('AMOUNT', 'PERCENT') NOT NULL,
                         discount_value DECIMAL(10,2) NOT NULL,
                         min_order_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                         started_at DATETIME NOT NULL,
                         expired_at DATETIME NOT NULL,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================
-- 16. COUPON_USAGES
-- =========================
CREATE TABLE coupon_usages (
                               coupon_usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               discount_amount DECIMAL(10,2) NOT NULL,
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
CREATE TABLE subscriptions (
                               subscription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               type ENUM('BASIC', 'PREMIUM', 'PRO') NOT NULL,
                               status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED', 'PAUSED') NOT NULL DEFAULT 'ACTIVE',
                               price DECIMAL(10,2) NOT NULL,
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
CREATE TABLE credits (
                         credit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         balance DECIMAL(10,2) NOT NULL DEFAULT 0,
                         updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
                         user_id BIGINT NOT NULL UNIQUE,
                         CONSTRAINT fk_credits_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- 19. INSTRUCTOR_PROFILES
-- =========================
-- 테이블 생성
CREATE TABLE instructor_profiles (
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

-- =========================
-- 20. INSTRUCTOR_BANK_ACCOUNTS
-- =========================
CREATE TABLE instructor_bank_accounts (
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
CREATE TABLE settlements (
                             settlement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             period_start DATE NOT NULL,
                             period_end DATE NOT NULL,
                             total_sales DECIMAL(10,2) NOT NULL DEFAULT 0,
                             commission_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                             settlement_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
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
CREATE TABLE notification_settings (
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
CREATE TABLE notifications (
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
CREATE TABLE email_verifications (
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

CREATE TABLE ai_prompts (
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
CREATE TABLE resumes (
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
CREATE TABLE job_postings (
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
CREATE TABLE skills (
                        skill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE,
                        category VARCHAR(100),
                        description TEXT,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 29. JOB_REQUIRED_SKILLS
-- =========================
CREATE TABLE job_required_skills (
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
CREATE TABLE course_skills (
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
CREATE TABLE ai_resume_evaluations (
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
CREATE TABLE ai_recommendations (
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

CREATE TABLE certifications (
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
CREATE TABLE external_exam_links (
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
CREATE TABLE roadmaps (
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
CREATE TABLE roadmap_steps (
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
CREATE TABLE inquiries (
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
CREATE TABLE inquiry_replies (
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
CREATE TABLE system_notices (
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
CREATE TABLE bookmarks (
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
CREATE TABLE lecture_notes (
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
CREATE TABLE reports (
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
CREATE TABLE admin_logs (
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
CREATE TABLE user_activity_logs (
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
CREATE TABLE d_days (
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

CREATE TABLE IF NOT EXISTS user_certifications (
                                                   user_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   user_id               BIGINT NOT NULL,
                                                   certification_name    VARCHAR(255) NOT NULL,
    issued_by             VARCHAR(255),
    issued_date           DATE,
    file_url              VARCHAR(500),
    status                ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    created_at            DATETIME DEFAULT NOW(),
    deleted_at            DATETIME NULL,
    CONSTRAINT fk_user_cert_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

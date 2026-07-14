-- be4/V6.4.1__add_admin_accounts.sql
-- 점검모드 등 admin 권한 테스트용 관리자 계정 admin02~admin07 추가
-- 비밀번호는 admin01과 동일한 해시 재사용 (평문: admin)

INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin02', 'admin02@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자02', '1985-01-01', '010-0000-0002', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN002'),
    ('admin03', 'admin03@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자03', '1985-01-01', '010-0000-0003', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN003'),
    ('admin04', 'admin04@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자04', '1985-01-01', '010-0000-0004', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN004'),
    ('admin05', 'admin05@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자05', '1985-01-01', '010-0000-0005', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN005'),
    ('admin06', 'admin06@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자06', '1985-01-01', '010-0000-0006', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN006'),
    ('admin07', 'admin07@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', '관리자07', '1985-01-01', '010-0000-0007', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN007');

-- 이미 존재하는데(예: login_id 충돌로 INSERT IGNORE가 스킵된 경우) 비밀번호/권한/상태가
-- 다르게 세팅돼 있을 수 있으므로 admin02~07은 강제로 ADMIN/ACTIVE/인증완료 + 동일 해시로 맞춤
UPDATE users
SET password = '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C',
    role = 'ADMIN',
    status = 'ACTIVE',
    email_verified = TRUE
WHERE login_id IN ('admin02', 'admin03', 'admin04', 'admin05', 'admin06', 'admin07');

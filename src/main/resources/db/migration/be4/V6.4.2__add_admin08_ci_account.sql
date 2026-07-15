-- be4/V6.4.2__add_admin08_ci_account.sql
-- CI/CD(deploy-backend.yml) 전용 점검모드 자동 on/off 계정.
-- admin01~07은 팀원 수동 테스트용이라, 배포 파이프라인이 같은 계정을 쓰면
-- 동시 로그인 차단(TokenVersionService) 때문에 팀원 세션이 배포 타이밍에
-- 로그아웃당할 수 있어 별도 계정으로 분리.
-- 비밀번호는 admin01과 동일한 해시 재사용 (평문: admin)

INSERT IGNORE INTO users
(login_id, email, password, name, birth_date, phone, role, status, email_verified, referral_code)
VALUES
    ('admin08', 'admin08@test.com', '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C', 'CI배포전용', '1985-01-01', '010-0000-0008', 'ADMIN', 'ACTIVE', TRUE, 'ADMIN008');

-- 이미 존재하는데(예: login_id 충돌로 INSERT IGNORE가 스킵된 경우) 비밀번호/권한/상태가
-- 다르게 세팅돼 있을 수 있으므로 강제로 ADMIN/ACTIVE/인증완료 + 동일 해시로 맞춤
UPDATE users
SET password = '$2a$10$HFStfIJEmYPf7MIpzQ4aSeqGSN.IWC4ZCUKHxKy7E/m4/xxWD1f4C',
    role = 'ADMIN',
    status = 'ACTIVE',
    email_verified = TRUE
WHERE login_id = 'admin08';

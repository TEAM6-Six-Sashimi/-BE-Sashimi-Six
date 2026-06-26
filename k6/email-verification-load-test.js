import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const p95ResponseTime = new Trend('p95_response_time');
const errorRate = new Rate('error_rate');
const signupRequests = new Counter('signup_verification_requests');
const passwordResetRequests = new Counter('password_reset_verification_requests');

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '40s', target: 30 },
        { duration: '40s', target: 50 },
        { duration: '180s', target: 50 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        error_rate: ['rate<0.05'],
    },
};

const BASE_URL = 'http://localhost:8080';

// 기존 더미 유저 이메일 (비밀번호 재설정용)
const EXISTING_EMAILS = [
    'student1@test.com',
    'student2@test.com',
    'student3@test.com',
    'instructor1@test.com',
    'instructor2@test.com',
];

export default function () {
    const params = { headers: { 'Content-Type': 'application/json' } };

    // VU 번호 기준으로 절반은 SIGNUP, 절반은 PASSWORD_RESET
    const isSignup = __VU % 2 === 0;

    let payload;

    if (isSignup) {
        payload = JSON.stringify({
            targetEmail: `bagminseo1999+signup${__VU}${__ITER}@gmail.com`,
            purpose: 'SIGNUP',
        });
        signupRequests.add(1);
    } else {
        payload = JSON.stringify({
            targetEmail: `bagminseo1999+reset${__VU}${__ITER}@gmail.com`,
            purpose: 'PASSWORD_RESET',
        });
        passwordResetRequests.add(1);
    }

    const res = http.post(`${BASE_URL}/verifications/email/request`, payload, params);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1s': (r) => r.timings.duration < 1000,
    });

    errorRate.add(!success);
    p95ResponseTime.add(res.timings.duration);

    sleep(1);
}

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

/**
 * 결제 멱등성 재사용 테스트
 *
 * 목적
 * - 같은 사용자가 같은 Idempotency-Key와 같은 결제 body를 여러 번 보냈을 때
 *   실제 결제는 한 번만 처리되고 이후 요청은 저장된 결제 결과를 재사용하는지 확인한다.
 *
 * Grafana/Prometheus에서 확인할 지표
 * - payment_idempotency_reused_total
 * - payment_duplicate_completed_total
 *
 * 기대 결과
 * - payment_idempotency_reused_total 값은 증가한다.
 * - payment_duplicate_completed_total 값은 0이어야 한다.
 *
 * 실행 예시
 * - 로컬: k6 run k6/payment-idempotency-reuse-test.js
 * - 배포: $env:BASE_URL="http://3.34.188.172:8080"; k6 run k6/payment-idempotency-reuse-test.js
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LOGIN_ID = __ENV.LOGIN_ID || 'k6_course_019';
const PASSWORD = __ENV.PASSWORD || 'student01!';
const COURSE_ID = Number(__ENV.COURSE_ID || 1);


export const options = {
    scenarios: {
        duplicate_click_users: {
            executor: 'constant-vus',

            // 동시에 같은 결제 버튼을 여러 번 누르는 상황을 만든다.
            // 이 숫자를 올리면 같은 멱등성 키로 들어오는 중복 요청이 많아진다.
            vus: 5,

            // 15초 동안 같은 요청을 반복한다.
            duration: '15s',
        },
    },
    thresholds: {
        // 중복 요청에서는 409 PROCESSING이 순간적으로 나올 수 있으므로
        // HTTP 실패율을 빡세게 보지 않고, 백엔드 지표 증가 여부를 Grafana에서 확인한다.
        k6_idempotency_reuse_unexpected_error_rate: ['rate<0.2'],
    },
};

const unexpectedErrorRate = new Rate('k6_idempotency_reuse_unexpected_error_rate');

function jsonHeaders(extraHeaders = {}) {
    return {
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            ...extraHeaders,
        },
    };
}

function login() {
    const response = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({
            loginId: LOGIN_ID,
            password: PASSWORD,
        }),
        jsonHeaders()
    );

    check(response, {
        'login success': (r) => r.status === 200 && !!r.json('accessToken'),
    });

    if (response.status !== 200 || !response.json('accessToken')) {
        throw new Error(`로그인 실패: status=${response.status}, body=${response.body}`);
    }

    return response.json('accessToken');
}

function checkout(token, idempotencyKey) {
    const requestBody = JSON.stringify({
        purchaseType: 'COURSE',
        courseId: COURSE_ID,
        planCode: null,
        agreed: true,
    });

    const response = http.post(
        `${BASE_URL}/payments/checkout`,
        requestBody,
        jsonHeaders({
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': idempotencyKey,
        })
    );

    const accepted = check(response, {
        'same idempotency request is handled': (r) =>
            r.status === 200 || r.status === 201 || r.status === 409,
    });

    unexpectedErrorRate.add(!accepted);
    return response;
}

export function setup() {
    const token = login();
    const idempotencyKey = `k6-reuse-course-${Date.now()}`;

    const firstResponse = checkout(token, idempotencyKey);

    if (firstResponse.status !== 200 && firstResponse.status !== 201) {
        throw new Error(`최초 결제 요청 실패: status=${firstResponse.status}, body=${firstResponse.body}`);
    }

    return {
        token,
        idempotencyKey,
    };
}

export default function (data) {
    checkout(data.token, data.idempotencyKey);
    sleep(0.2);
}

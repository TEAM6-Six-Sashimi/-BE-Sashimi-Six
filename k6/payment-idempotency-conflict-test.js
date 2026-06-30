import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

/**
 * 결제 멱등성 충돌 테스트
 *
 * 목적
 * - 같은 사용자가 같은 Idempotency-Key를 사용하면서 결제 body를 바꾸면
 *   백엔드가 다른 결제 요청으로 판단하고 차단하는지 확인한다.
 *
 * Grafana/Prometheus에서 확인할 지표
 * - payment_idempotency_conflict_total
 * - payment_duplicate_completed_total
 *
 * 기대 결과
 * - payment_idempotency_conflict_total 값은 증가한다.
 * - payment_duplicate_completed_total 값은 0이어야 한다.
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LOGIN_ID = __ENV.LOGIN_ID || 'k6_course_020';
const PASSWORD = __ENV.PASSWORD || 'student01!';
const FIRST_COURSE_ID = Number(__ENV.FIRST_COURSE_ID || 2);
const SECOND_COURSE_ID = Number(__ENV.SECOND_COURSE_ID || 3);


export const options = {
    scenarios: {
        conflict_users: {
            executor: 'constant-vus',
            vus: 3,
            duration: '10s',
        },
    },
    thresholds: {
        k6_idempotency_conflict_unexpected_error_rate: ['rate<0.2'],
    },
};

const unexpectedErrorRate = new Rate('k6_idempotency_conflict_unexpected_error_rate');

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

function checkout(token, courseId, idempotencyKey) {
    const requestBody = JSON.stringify({
        purchaseType: 'COURSE',
        courseId,
        planCode: null,
        agreed: true,
    });

    return http.post(
        `${BASE_URL}/payments/checkout`,
        requestBody,
        jsonHeaders({
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': idempotencyKey,
        })
    );
}

export function setup() {
    const token = login();
    const idempotencyKey = `k6-conflict-course-${Date.now()}`;

    const firstResponse = checkout(token, FIRST_COURSE_ID, idempotencyKey);

    if (firstResponse.status !== 200 && firstResponse.status !== 201) {
        throw new Error(`최초 결제 요청 실패: status=${firstResponse.status}, body=${firstResponse.body}`);
    }

    return {
        token,
        idempotencyKey,
    };
}

export default function (data) {
    const response = checkout(
        data.token,
        SECOND_COURSE_ID,
        data.idempotencyKey
    );

    const accepted = check(response, {
        'different request with same idempotency key is blocked': (r) => r.status === 409,
    });

    unexpectedErrorRate.add(!accepted);
    sleep(0.2);
}

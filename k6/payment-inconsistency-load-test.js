import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

/**
 * 토스 승인-크레딧 반영 불일치 지표 확인용 테스트
 *
 * 목적
 * - 정상 API 호출이 진행되는 동안 toss_credit_charge_inconsistency_total이 0으로 유지되는지 확인한다.
 *
 * 주의
 * - k6에서 실제 토스 QR 결제를 자동으로 완료하기는 어렵다.
 * - 실제 토스 결제 성공 후에는 프론트/스웨거로 충전 1건을 수행하고,
 *   Grafana에서 아래 쿼리가 0인지 확인하면 된다.
 *
 * Grafana/Prometheus 쿼리
 * - sum(increase(toss_credit_charge_inconsistency_total[5m])) or vector(0)
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LOGIN_ID = __ENV.LOGIN_ID || 'k6_course_003';
const PASSWORD = __ENV.PASSWORD || 'student01!';

export const options = {
    stages: [
        // 이 숫자를 올리면 크레딧 잔액 조회 트래픽이 증가한다.
        { duration: '10s', target: 5 },
        { duration: '30s', target: 5 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        k6_credit_balance_error_rate: ['rate<0.05'],
    },
};

const creditBalanceErrorRate = new Rate('k6_credit_balance_error_rate');

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

export function setup() {
    return {
        token: login(),
    };
}

export default function (data) {
    const response = http.get(
        `${BASE_URL}/credits/me`,
        jsonHeaders({
            Authorization: `Bearer ${data.token}`,
        })
    );

    const ok = check(response, {
        'credit balance status is 200': (r) => r.status === 200,
    });

    creditBalanceErrorRate.add(!ok);
    sleep(0.2);
}

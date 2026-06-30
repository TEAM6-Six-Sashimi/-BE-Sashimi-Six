import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

/**
 * 결제 처리시간 확인용 선택 테스트
 *
 * 현재 모듈 4의 핵심 발표 지표는 중복 결제 방어, 토스-크레딧 불일치, 결제 전환율이다.
 * 이 파일은 처리시간을 보조로 보고 싶을 때만 실행한다.
 *
 * 자동 로그인
 * - TOKEN을 직접 넣지 않는다.
 * - k6_cart_020 계정으로 로그인한 뒤 장바구니 결제를 1회성 트래픽으로 호출한다.
 *
 * 주의
 * - 실제 주문/결제 데이터가 생성된다.
 * - 같은 로컬 DB에서 여러 번 반복 실행하면 장바구니가 비거나 이미 구매한 강의 때문에 실패할 수 있다.
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LOGIN_ID = __ENV.LOGIN_ID || 'k6_cart_020';
const PASSWORD = __ENV.PASSWORD || 'student01!';

const checkoutRequests = new Counter('k6_payment_checkout_requests');
const checkoutErrorRate = new Rate('k6_payment_checkout_error_rate');
const checkoutResponseTime = new Trend('k6_payment_checkout_response_time');

export const options = {
    scenarios: {
        optional_checkout_duration_users: {
            executor: 'shared-iterations',

            // 실제 결제 데이터를 만들기 때문에 기본은 아주 작게 둔다.
            // 숫자를 키우면 결제 요청 수가 증가한다.
            vus: 1,
            iterations: 1,
            maxDuration: '30s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        k6_payment_checkout_error_rate: ['rate<0.3'],
    },
};

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
    const requestBody = JSON.stringify({
        purchaseType: 'CART',
        courseId: null,
        planCode: null,
        agreed: true,
    });

    checkoutRequests.add(1);

    const response = http.post(
        `${BASE_URL}/payments/checkout`,
        requestBody,
        jsonHeaders({
            Authorization: `Bearer ${data.token}`,
            'Idempotency-Key': `k6-duration-cart-${Date.now()}-${__VU}-${__ITER}`,
        })
    );

    const success = check(response, {
        'checkout success status': (r) => r.status === 200 || r.status === 201,
        'checkout response time < 1s': (r) => r.timings.duration < 1000,
    });

    checkoutErrorRate.add(!success);
    checkoutResponseTime.add(response.timings.duration);

    sleep(1);
}

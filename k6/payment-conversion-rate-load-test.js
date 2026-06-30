import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import exec from 'k6/execution';

/**
 * 결제 전환율 시나리오 테스트
 *
 * 목적
 * - 강의 단일 결제, 장바구니 결제, AI 구독권 결제의 preview 대비 checkout 전환율을 만든다.
 * - 발표 시나리오상 장바구니가 가장 낮은 전환율을 보이도록 구성한다.
 *
 * Grafana/Prometheus에서 확인할 지표
 * - payment_preview_requests_total
 * - payment_completed_total
 *
 * 의도한 시나리오
 * - COURSE: preview 20명, checkout 14명  -> 약 70%
 * - CART: preview 20명, checkout 4명      -> 약 20%, 가장 낮음
 * - AI_SUBSCRIPTION: preview 10명, checkout 5명 -> 약 50%
 *
 * 총 실제 결제 성공 목표
 * - 14 + 4 + 5 = 23건
 * - 사용자가 말한 20~30건 결제 범위 안에 들어간다.
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PASSWORD = __ENV.PASSWORD || 'student01!';
const PLAN_CODE = __ENV.PLAN_CODE || 'MONTHLY';

const COURSE_PREVIEW_COUNT = Number(__ENV.COURSE_PREVIEW_COUNT || 20);
const COURSE_CHECKOUT_COUNT = Number(__ENV.COURSE_CHECKOUT_COUNT || 14);
const CART_PREVIEW_COUNT = Number(__ENV.CART_PREVIEW_COUNT || 20);
const CART_CHECKOUT_COUNT = Number(__ENV.CART_CHECKOUT_COUNT || 4);
const SUBSCRIPTION_PREVIEW_COUNT = Number(__ENV.SUBSCRIPTION_PREVIEW_COUNT || 10);
const SUBSCRIPTION_CHECKOUT_COUNT = Number(__ENV.SUBSCRIPTION_CHECKOUT_COUNT || 5);

const previewErrorRate = new Rate('k6_payment_preview_error_rate');
const checkoutErrorRate = new Rate('k6_payment_checkout_error_rate');

export const options = {
    scenarios: {
        course_preview_users: {
            executor: 'shared-iterations',
            exec: 'coursePreview',
            vus: 5,
            iterations: COURSE_PREVIEW_COUNT,
            maxDuration: '1m',
        },
        course_checkout_users: {
            executor: 'shared-iterations',
            exec: 'courseCheckout',
            vus: 5,
            iterations: COURSE_CHECKOUT_COUNT,
            maxDuration: '1m',
            startTime: '3s',
        },
        cart_preview_users: {
            executor: 'shared-iterations',
            exec: 'cartPreview',
            vus: 5,
            iterations: CART_PREVIEW_COUNT,
            maxDuration: '1m',
            startTime: '6s',
        },
        cart_checkout_users: {
            executor: 'shared-iterations',
            exec: 'cartCheckout',
            vus: 2,
            iterations: CART_CHECKOUT_COUNT,
            maxDuration: '1m',
            startTime: '9s',
        },
        subscription_preview_users: {
            executor: 'shared-iterations',
            exec: 'subscriptionPreview',
            vus: 3,
            iterations: SUBSCRIPTION_PREVIEW_COUNT,
            maxDuration: '1m',
            startTime: '12s',
        },
        subscription_checkout_users: {
            executor: 'shared-iterations',
            exec: 'subscriptionCheckout',
            vus: 2,
            iterations: SUBSCRIPTION_CHECKOUT_COUNT,
            maxDuration: '1m',
            startTime: '15s',
        },
    },
    thresholds: {
        k6_payment_preview_error_rate: ['rate<0.05'],
        k6_payment_checkout_error_rate: ['rate<0.1'],
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

function login(loginId) {
    const response = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({
            loginId,
            password: PASSWORD,
        }),
        jsonHeaders()
    );

    check(response, {
        [`login success - ${loginId}`]: (r) => r.status === 200 && !!r.json('accessToken'),
    });

    if (response.status !== 200 || !response.json('accessToken')) {
        throw new Error(`로그인 실패: loginId=${loginId}, status=${response.status}, body=${response.body}`);
    }

    return response.json('accessToken');
}

function numberedLoginId(prefix, index) {
    return `${prefix}_${String(index).padStart(3, '0')}`;
}

function buildUsers(prefix, count) {
    const users = [];

    for (let i = 1; i <= count; i += 1) {
        const loginId = numberedLoginId(prefix, i);
        users.push({
            loginId,
            token: login(loginId),
        });
    }

    return users;
}

function authHeaders(token, extraHeaders = {}) {
    return jsonHeaders({
        Authorization: `Bearer ${token}`,
        ...extraHeaders,
    });
}

function courseIdByIndex(index) {
    // 로컬 더미 DB에 있는 강의 ID 범위 안에서 분산한다.
    // 강의 ID 1~5를 반복 사용한다.
    return (index % 5) + 1;
}

function idempotencyKey(prefix, user) {
    // 실제 checkout은 모두 서로 다른 결제 시도이므로 고유 키를 사용한다.
    return `${prefix}-${user.loginId}-${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

function recordPreview(response, label) {
    const ok = check(response, {
        [`${label} preview success`]: (r) => r.status === 200,
    });

    previewErrorRate.add(!ok);
}

function recordCheckout(response, label) {
    const ok = check(response, {
        [`${label} checkout success`]: (r) => r.status === 200 || r.status === 201,
    });

    checkoutErrorRate.add(!ok);
}

export function setup() {
    return {
        courseUsers: buildUsers('k6_course', 20),
        cartUsers: buildUsers('k6_cart', 20),
        subscriptionUsers: buildUsers('k6_sub', 10),
    };
}

export function coursePreview(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.courseUsers[index % data.courseUsers.length];
    const courseId = courseIdByIndex(index);

    const response = http.get(
        `${BASE_URL}/payments/course/${courseId}/preview`,
        authHeaders(user.token)
    );

    recordPreview(response, 'course');
    sleep(0.1);
}

export function courseCheckout(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.courseUsers[index % data.courseUsers.length];
    const courseId = courseIdByIndex(index);

    const response = http.post(
        `${BASE_URL}/payments/checkout`,
        JSON.stringify({
            purchaseType: 'COURSE',
            courseId,
            planCode: null,
            agreed: true,
        }),
        authHeaders(user.token, {
            'Idempotency-Key': idempotencyKey('k6-course-checkout', user),
        })
    );

    recordCheckout(response, 'course');
    sleep(0.1);
}

export function cartPreview(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.cartUsers[index % data.cartUsers.length];

    const response = http.get(
        `${BASE_URL}/payments/cart/preview`,
        authHeaders(user.token)
    );

    recordPreview(response, 'cart');
    sleep(0.1);
}

export function cartCheckout(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.cartUsers[index % data.cartUsers.length];

    const response = http.post(
        `${BASE_URL}/payments/checkout`,
        JSON.stringify({
            purchaseType: 'CART',
            courseId: null,
            planCode: null,
            agreed: true,
        }),
        authHeaders(user.token, {
            'Idempotency-Key': idempotencyKey('k6-cart-checkout', user),
        })
    );

    recordCheckout(response, 'cart');
    sleep(0.1);
}

export function subscriptionPreview(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.subscriptionUsers[index % data.subscriptionUsers.length];

    const response = http.get(
        `${BASE_URL}/subscriptions/plans/${PLAN_CODE}/preview`,
        authHeaders(user.token)
    );

    recordPreview(response, 'subscription');
    sleep(0.1);
}

export function subscriptionCheckout(data) {
    const index = exec.scenario.iterationInTest;
    const user = data.subscriptionUsers[index % data.subscriptionUsers.length];

    const response = http.post(
        `${BASE_URL}/payments/checkout`,
        JSON.stringify({
            purchaseType: 'AI_SUBSCRIPTION',
            courseId: null,
            planCode: PLAN_CODE,
            agreed: true,
        }),
        authHeaders(user.token, {
            'Idempotency-Key': idempotencyKey('k6-subscription-checkout', user),
        })
    );

    recordCheckout(response, 'subscription');
    sleep(0.1);
}

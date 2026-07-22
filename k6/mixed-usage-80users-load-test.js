import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

/**
 * 80명 계단식 혼합 시나리오 부하테스트
 *
 * 구성 (총 80명)
 * - 40명: 강의 목록 -> 상세(미리보기 영상 URL 포함) 조회, 비로그인
 *   (course detail 응답에 이미 presigned 영상 URL이 들어있어 별도 재생 API 없음)
 * - 20명: 챗봇 메시지 전송, 비로그인 (POST /api/chatbot/messages는 인증 불필요)
 * - 20명: 구독권 계정(test61~test80)으로 이력서/자소서/채용공고 AI 기능을 번갈아 사용
 *
 * 주의 (실행 전 필독)
 * - 이 테스트를 돌리는 동안 점검모드(POST /admin/maintenance/enable)는 꺼져 있어야 함.
 *   test61~80 계정은 STUDENT 권한이라 점검모드가 켜져 있으면 /auth/login 이후의
 *   모든 요청이 503(COMMON_900)으로 막힘 (ROLE_ADMIN만 우회 가능한 구조).
 * - AI 기능은 계정당 기능별 시간당 10회, 전체 시간당 30회 제한이 있음
 *   (AiRateLimitPolicy). 테스트가 몇 분 안 되는 짧은 시간이라도 반복 호출하면
 *   금방 AI_RATE_LIMIT_EXCEEDED로 실패가 잡힐 수 있음 — 이건 인프라 문제가
 *   아니라 설계된 제한이니 결과 해석할 때 별도로 구분해서 볼 것.
 * - 이력서(POST /resumes)는 계정당 1개만 만들 수 있어서, 첫 이터레이션에서
 *   만들고 이후엔 재사용함(있으면 스킵).
 *
 * 실행 예시
 *   k6 run -e BASE_URL=https://api.sixsashimi.com.market-app.org k6/mixed-usage-80users-load-test.js
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PASSWORD = 'test';

// test61~test80 (구독권 + ai_consent 세팅된 계정)
const AI_ACCOUNTS = Array.from({ length: 20 }, (_, i) => `test${61 + i}`);

const errorRate = new Rate('k6_error_rate');
const rateLimitedRate = new Rate('k6_ai_rate_limited_rate');
const previewDuration = new Trend('k6_preview_duration', true);
const chatbotDuration = new Trend('k6_chatbot_duration', true);
const aiFeatureDuration = new Trend('k6_ai_feature_duration', true);

export const options = {
    scenarios: {
        preview_watchers: {
            executor: 'ramping-vus',
            exec: 'previewWatcher',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 10 },
                { duration: '1m', target: 20 },
                { duration: '1m', target: 40 },
                { duration: '2m', target: 40 },
                { duration: '30s', target: 0 },
            ],
        },
        chatbot_users: {
            executor: 'ramping-vus',
            exec: 'chatbotUser',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 5 },
                { duration: '1m', target: 10 },
                { duration: '1m', target: 20 },
                { duration: '2m', target: 20 },
                { duration: '30s', target: 0 },
            ],
        },
        ai_feature_users: {
            executor: 'ramping-vus',
            exec: 'aiFeatureUser',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 5 },
                { duration: '1m', target: 10 },
                { duration: '1m', target: 20 },
                { duration: '2m', target: 20 },
                { duration: '30s', target: 0 },
            ],
        },
    },
    thresholds: {
        k6_error_rate: ['rate<0.05'],
        http_req_duration: ['p(95)<3000'],
    },
};

// k6는 VU마다 독립된 JS 인스턴스라, 모듈 스코프 변수가 자연스럽게 "VU별 캐시"로 동작함.
let cachedToken = null;
let cachedLoginId = null;
let resumeCreated = false;

function authHeaders(loginId) {
    if (cachedToken && cachedLoginId === loginId) {
        return { headers: { Authorization: `Bearer ${cachedToken}` } };
    }

    const res = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({ loginId, password: PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    const token = res.json('accessToken');
    if (!token) {
        return null;
    }
    cachedToken = token;
    cachedLoginId = loginId;
    return { headers: { Authorization: `Bearer ${token}` } };
}

// 그룹 A (40명): 강의 목록 -> 상세 조회, 미리보기 영상 시청 흉내
export function previewWatcher() {
    const listRes = http.get(`${BASE_URL}/api/courses`);
    const listOk = check(listRes, { 'course list 200': (r) => r.status === 200 });
    errorRate.add(!listOk);

    if (listOk) {
        let courses = [];
        try { courses = listRes.json(); } catch (e) { /* noop */ }

        if (Array.isArray(courses) && courses.length > 0) {
            const picked = courses[Math.floor(Math.random() * courses.length)];
            if (picked.courseId) {
                const start = Date.now();
                const detailRes = http.get(`${BASE_URL}/api/courses/${picked.courseId}`);
                const detailOk = check(detailRes, { 'course detail 200': (r) => r.status === 200 });
                errorRate.add(!detailOk);
                previewDuration.add(Date.now() - start);

                // 영상 URL은 이미 상세 응답에 포함돼있어 별도 호출 없음 - 시청 시간만 흉내
                sleep(Math.random() * 5 + 5);
                return;
            }
        }
    }
    sleep(2);
}

// 그룹 B (20명): 챗봇 메시지 (비로그인)
export function chatbotUser() {
    const start = Date.now();
    const res = http.post(
        `${BASE_URL}/api/chatbot/messages`,
        JSON.stringify({ message: '정보처리기사 시험 일정 알려줘', history: [] }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    const ok = check(res, { 'chatbot 200': (r) => r.status === 200 });
    errorRate.add(!ok);
    chatbotDuration.add(Date.now() - start);

    sleep(Math.random() * 3 + 2);
}

// 그룹 C (20명, test61~80): 이력서/자소서/채용공고 AI 기능을 VU별로 하나씩 고정 배정
// 계정당 시간당 요청 제한(기능별 10회/전체 30회)이 있어서, VU당 무한 반복이 아니라
// 최대 2번만 실제 호출하고 이후엔 대기함 (실제 사용자도 짧은 시간에 같은 기능을
// 수십 번 다시 안 씀 - 더 현실적인 패턴이기도 함).
const MAX_AI_CALLS_PER_VU = 1;
let aiCallCount = 0;

export function aiFeatureUser() {
    if (aiCallCount >= MAX_AI_CALLS_PER_VU) {
        sleep(5);
        return;
    }
    aiCallCount++;

    const idx = (__VU - 1) % AI_ACCOUNTS.length;
    const loginId = AI_ACCOUNTS[idx];
    const params = authHeaders(loginId);

    if (!params) {
        errorRate.add(1);
        sleep(2);
        return;
    }

    const jsonHeaders = { headers: { ...params.headers, 'Content-Type': 'application/json' } };
    const feature = idx % 3; // 0: 자소서, 1: 이력서, 2: 채용공고
    const start = Date.now();
    let res;

    if (feature === 0) {
        http.put(`${BASE_URL}/cover-letters`, JSON.stringify({
            items: [{
                questionKey: 'SELF_INTRODUCTION',
                content: '저는 성실하고 책임감 있는 지원자입니다. 이 회사에 꼭 입사하고 싶습니다.',
            }],
        }), jsonHeaders);

        res = http.post(`${BASE_URL}/cover-letters/review`, null, params);
    } else if (feature === 1) {
        if (!resumeCreated) {
            http.post(`${BASE_URL}/resumes`, JSON.stringify({
                educations: [{
                    schoolName: '테스트대학교',
                    startYearMonth: '2020-03',
                    endYearMonth: '2024-02',
                    degree: 'BACHELOR',
                    major: '컴퓨터공학',
                    graduationStatus: 'GRADUATED',
                }],
                entryLevel: true,
                careers: [],
                certifications: [],
                defaultResume: true,
            }), jsonHeaders);
            resumeCreated = true;
        }

        const meRes = http.get(`${BASE_URL}/resumes`, params);
        let resumeId = null;
        try { resumeId = meRes.json('resumeId'); } catch (e) { /* noop */ }

        if (resumeId) {
            res = http.post(`${BASE_URL}/resumes/${resumeId}/ai-review`, null, params);
        }
    } else {
        res = http.post(`${BASE_URL}/recommendations/job-posting`, JSON.stringify({
            inputType: 'TEXT',
            rawContent: '백엔드 개발자 채용공고: Java, Spring Boot 경험자 우대, 3년 이상 경력',
        }), jsonHeaders);
    }

    if (res) {
        const rateLimited = res.status === 429;
        rateLimitedRate.add(rateLimited);

        const ok = rateLimited || [200, 201, 202].includes(res.status);
        errorRate.add(!ok);
    } else {
        errorRate.add(1);
    }
    aiFeatureDuration.add(Date.now() - start);

    sleep(Math.random() * 5 + 5);
}

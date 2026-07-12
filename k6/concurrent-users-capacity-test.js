import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

/**
 * 동시접속자 처리량 테스트 (익명 사용자 브라우징 시나리오)
 *
 * 목적
 * - 로그인 없이도 가능한 가장 흔한 흐름(강의 목록 -> 강의 상세)을 반복하며
 *   동시 VU 수를 계단식으로 늘려서, 어느 시점부터 응답시간/에러율이 무너지는지 확인한다.
 * - t3.micro(EC2, 1GB RAM 6개 컨테이너 공유) + db.t3.micro(RDS)라는 스펙상
 *   메모리/CPU 크레딧이 진짜 병목인지 확인하는 게 목적이라, 실행 중에는
 *   EC2에 SSH로 접속해서 `free -h`, `docker stats`도 같이 관찰할 것을 권장한다.
 *   (Prometheus가 지금 컨테이너/호스트 레벨 메모리는 안 걷고 있어서 Grafana만으론 안 보임)
 *
 * 실행 예시
 *   k6 run k6/concurrent-users-capacity-test.js
 *   k6 run -e BASE_URL=https://api.sixsashimi.com k6/concurrent-users-capacity-test.js
 *
 * Grafana/Prometheus에서 같이 볼 지표
 * - hikaricp_connections_pending{job="sashimi"} (풀 대기 발생 여부)
 * - http_server_requests_seconds (엔드포인트별 응답시간)
 * - jvm_memory_used_bytes (힙 사용량 — 호스트 전체 메모리는 아님, 참고용)
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('k6_browse_error_rate');
const courseListDuration = new Trend('k6_course_list_duration', true);
const courseDetailDuration = new Trend('k6_course_detail_duration', true);

export const options = {
    scenarios: {
        concurrent_browsing: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 10 },
                { duration: '1m', target: 10 },
                { duration: '30s', target: 20 },
                { duration: '1m', target: 20 },
                { duration: '30s', target: 30 },
                { duration: '1m', target: 30 },
                { duration: '30s', target: 50 },
                { duration: '1m', target: 50 },
                { duration: '30s', target: 80 },
                { duration: '1m', target: 80 },
                { duration: '30s', target: 0 },
            ],
        },
    },
    thresholds: {
        k6_browse_error_rate: ['rate<0.05'],
        http_req_duration: ['p(95)<2000'],
    },
};

export default function () {
    const listRes = http.get(`${BASE_URL}/api/courses`);
    const listOk = check(listRes, {
        'course list status 200': (r) => r.status === 200,
    });
    errorRate.add(!listOk);
    courseListDuration.add(listRes.timings.duration);

    if (listOk) {
        let courses = [];
        try {
            courses = listRes.json();
        } catch (e) {
            courses = [];
        }

        if (Array.isArray(courses) && courses.length > 0) {
            const picked = courses[Math.floor(Math.random() * courses.length)];
            const courseId = picked.courseId;

            if (courseId) {
                const detailRes = http.get(`${BASE_URL}/api/courses/${courseId}`);
                const detailOk = check(detailRes, {
                    'course detail status 200': (r) => r.status === 200,
                });
                errorRate.add(!detailOk);
                courseDetailDuration.add(detailRes.timings.duration);
            }
        }
    }

    // 실제 사용자의 페이지 읽는 시간(think time) 흉내
    sleep(Math.random() * 2 + 1);
}

import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '20s', target: 30 },
        { duration: '30s', target: 30 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<1000'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN = __ENV.ACCESS_TOKEN;

export default function () {
    const payload = JSON.stringify({
        inputType: 'TEXT',
        rawContent:
            '백엔드 개발자 채용 공고입니다. 주요 업무는 Java와 Spring Boot 기반 REST API 개발, MySQL 데이터베이스 설계 및 운영입니다. 필수 자격요건은 Java 개발 경험과 Spring Boot 사용 경험입니다. 우대사항으로 정보처리기사 자격증 보유자를 우대합니다. 추천 자격증은 정보처리기사입니다.',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${TOKEN}`,
        },
    };

    const res = http.post(`${BASE_URL}/recommendations/job-posting`, payload, params);

    check(res, {
        'status is 202': (r) => r.status === 202,
        'has recommendationId': (r) => {
            try {
                return JSON.parse(r.body).recommendationId !== undefined;
            } catch (e) {
                return false;
            }
        },
    });

    sleep(1);
}
#!/bin/bash
# AuthService.reissue()/logout() 쿼리 감소 효과를 배포 전/후로 비교하기 위한 간단한 응답시간 측정.
# k6 없이 curl만으로, 소수 요청을 반복해서 각 엔드포인트의 평균 응답시간(ms)을 잰다.
#
# 사용법:
#   BASE_URL=https://api.sixsashimi.com.market-app.org ./before-after-latency-check.sh
#
# 배포 전에 한 번, 배포 후에 한 번 돌려서 두 결과를 비교하면 됨.

BASE_URL="${BASE_URL:-http://localhost:8080}"
LOGIN_ID="${LOGIN_ID:-test61}"
PASSWORD="${PASSWORD:-test}"
REPEAT="${REPEAT:-20}"

echo "대상: $BASE_URL / 계정: $LOGIN_ID / 반복: ${REPEAT}회"
echo

avg_time() {
    local label="$1"; shift
    local times=()
    for i in $(seq 1 "$REPEAT"); do
        times+=("$("$@")")
    done
    local avg=$(printf '%s\n' "${times[@]}" | awk '{sum+=$1} END {printf "%.2f", (sum/NR)*1000}')
    echo "[$label] 평균 ${avg}ms (${REPEAT}회, 개별값(초): ${times[*]})"
}

login_once() {
    curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"loginId\":\"$LOGIN_ID\",\"password\":\"$PASSWORD\"}"
}

RESPONSE=$(login_once)
ACCESS_TOKEN=$(echo "$RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo "$RESPONSE" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    echo "로그인 실패, 응답: $RESPONSE"
    exit 1
fi

echo "--- /auth/reissue ---"
avg_time "reissue" curl -s -o /dev/null -w "%{time_total}" -X POST "$BASE_URL/auth/reissue" \
    -H "Content-Type: application/json" \
    -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

echo
echo "--- /auth/logout ---"
# logout은 한 번 하면 refreshToken이 소모되므로, 매 반복 로그인 -> 로그아웃으로 측정
logout_after_fresh_login() {
    local r=$(login_once)
    local at=$(echo "$r" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    local rt=$(echo "$r" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)
    curl -s -o /dev/null -w "%{time_total}" -X POST "$BASE_URL/auth/logout" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $at" \
        -d "{\"refreshToken\":\"$rt\"}"
}
avg_time "logout" logout_after_fresh_login

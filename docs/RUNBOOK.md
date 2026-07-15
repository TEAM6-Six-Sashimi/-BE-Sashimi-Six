# 운영 런북 (Runbook)

배포/장애 상황에서 "누가 뭘 어떻게 하는지"를 정리한 문서입니다. 대부분 2026-07-15 실제로
겪은 상황을 그대로 반영했습니다.

---

## 1. 배포 (Deploy)

`develop` 브랜치에 머지되면 GitHub Actions(`deploy-backend.yml`)가 자동 실행됩니다.

**순서**: 배포 직전 점검모드 자동 ON → 빌드(`latest` + 커밋 SHA 태그) → 컨테이너 교체 →
헬스체크(최대 150초) → 오래된 이미지 태그 정리(최근 5개만 보관) → 점검모드 자동 OFF →
Grafana 배포 annotation 등록.

**수동으로 다시 배포하고 싶을 때**: GitHub → Actions → "Deploy Backend" → Run workflow
(develop 브랜치 선택). 코드 변경이 없어도 재배포만 다시 트리거 가능.

**주의**: `MAINTENANCE_ADMIN_LOGIN_ID`/`MAINTENANCE_ADMIN_PASSWORD` Secret이 등록돼 있어야
점검모드 자동 연동이 동작합니다. 없으면 조용히 건너뛰고 배포는 평소대로 진행됩니다(안전).

---

## 2. 롤백 (Rollback)

배포 후 문제가 생겼는데 원인 파악에 시간이 걸릴 것 같으면, **재빌드 없이** 즉시 이전 버전으로
되돌릴 수 있습니다.

1. 되돌릴 커밋의 short SHA 확인 — 이전 배포 성공 로그의 `Backend health check passed.
   Image tag: xxxxxxx` 부분, 또는 `git log --oneline`
2. GitHub → Actions → "Rollback Backend" → Run workflow → `image_tag`에 그 SHA 입력
3. 재빌드 없이 몇 초~몇십 초 안에 해당 버전으로 컨테이너 교체됨 (배포와 동일하게 점검모드
   자동 on/off 포함)

**로컬에 그 이미지가 없으면** 실패하고, 사용 가능한 태그 목록이 Actions 로그에 출력됩니다.
최근 5개 버전만 보관되므로 그보다 오래된 버전으로는 롤백 불가 — 이 경우 해당 커밋으로
`git revert` 후 재배포 필요.

---

## 3. 점검모드 수동 제어

CI/CD가 자동으로 켜고 끄지만, 정기점검이나 수동 부하테스트 시에는 직접 켜야 합니다.

```bash
# 로그인해서 토큰 받기
curl -X POST https://api.sixsashimi.com.market-app.org/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin01","password":"admin"}'

# 켜기
curl -X POST https://api.sixsashimi.com.market-app.org/admin/maintenance/enable \
  -H "Authorization: Bearer <토큰>" \
  -H "Content-Type: application/json" \
  -d '{"message":"정기점검 중입니다. n시 n분에 종료 예정입니다."}'

# 끄기
curl -X POST https://api.sixsashimi.com.market-app.org/admin/maintenance/disable \
  -H "Authorization: Bearer <토큰>"
```

**주의사항**
- 토큰은 3시간 만료. 오래된 토큰 재사용하면 만료돼서 실패할 수 있음 — 안 되면 새로 로그인부터.
- **부하테스트용 계정(k6가 쓰는 계정)과 수동 토글용 계정을 분리해서 쓸 것.** 같은 계정으로
  동시에 로그인하면 "동시 로그인 차단" 기능 때문에 서로의 토큰을 무효화시킴. (`admin01` =
  k6/일반 테스트, `admin02`~`07` = 팀원 수동 테스트, `admin08` = CI 전용, 비밀번호 전부 `admin`)
- ADMIN 세션은 점검모드 중에도 모든 기능 정상 이용 가능 (전용 계정으로 로그인해서 테스트).

---

## 4. 로컬 개발 환경 접속 (SSH 터널)

로컬에서 `local` 프로필로 실행하면 공유 개발 DB(`sixsashimi_dev`, RDS)에 SSH 터널로 접속합니다.

```bash
ssh -i "sashimi-key (1).pem" -L 3307:sashimi-db.c3a84ssqgmc2.ap-northeast-2.rds.amazonaws.com:3306 ubuntu@54.180.81.89
```

**자주 하는 실수 모음** (전부 실제로 겪은 것들)
- 이 터널 창을 계속 열어둬야 함 — 닫으면 연결도 같이 끊김.
- 터널 창 **안에서**(즉 EC2 서버 내부 쉘에서) `mysql -h 127.0.0.1 -P 3307 ...`를 치면 안 됨 —
  반드시 **별도의 새 터미널(로컬)**에서 쳐야 함.
- Windows에서 `ssh -f -N`(백그라운드) 옵션은 멈추고 Ctrl+C도 안 먹는 버그가 있음 — 쓰지 말 것.
  foreground로 열고 탭/창을 따로 써서 관리.
- 이미 열린 터널이 있는 상태에서 새로 열려고 하면 `bind: Permission denied`(포트 충돌) 발생.
  `netstat -ano | findstr :3307`로 기존 프로세스 찾아서 정리 후 재시도.

---

## 5. DB 마이그레이션(Flyway) 문제 대응

### "Migration checksum mismatch" 에러

**원인**: 이미 팀원 누군가의 로컬 실행으로 `sixsashimi_dev`에 적용된 마이그레이션 파일을,
나중에 누군가 내용을 수정해서 커밋한 경우 발생. Flyway는 이미 적용된 마이그레이션 파일의
내용이 바뀌면 체크섬 불일치로 막습니다. (2026-07-15 `V6.4.1`에서 실제로 발생)

**대응** (팀원 중 한 명만 하면 됨 — `sixsashimi_dev`는 공유 DB이므로 전원이 각자 할 필요 없음):
```sql
-- SSH 터널 연결 후, mysql 클라이언트로 sixsashimi_dev에 접속해서
DELETE FROM flyway_schema_history WHERE version = '해당 버전';
```
그 후 아무나 로컬 앱을 한 번 실행하면 Flyway가 최신 내용으로 재적용하며 체크섬을 새로 기록합니다.
(재실행해도 안전한 멱등성 있는 마이그레이션인지 먼저 확인할 것 — `INSERT IGNORE` 패턴이면 안전)

**재발 방지 원칙**: **한 번 커밋되어 팀원이 실행한 마이그레이션 파일은 절대 다시 수정하지 않는다.**
추가 수정이 필요하면 반드시 새 버전 파일(예: `V6.4.3`)을 만들 것.

### 운영 DB(`sixsashimi_db`)는 왜 이 문제가 없는가
운영은 `spring.flyway.enabled: false`로 Flyway 자동 마이그레이션이 꺼져 있어서 체크섬 검증 자체를
안 함. 운영 DB 반영은 DataGrip 콘솔 등으로 **직접 SQL 실행**하는 수동 방식.

---

## 6. 모니터링 / 장애 원인 파악 순서

문제(느려짐, 에러 증가 등) 발생 시 이 순서로 확인:

1. **Grafana** (`http://<EC2 IP>:3000`, 대시보드 "Sashimi Overview")
   - "타겟 Up/Down" — 배포 다운타임 여부
   - "JVM 힙 메모리 사용량" — 힙 압박 여부
   - "DB 커넥션 풀 현황 (HikariCP)" — 대기 커넥션 발생 여부 (2026-07-15에 실제 병목 원인이었음)
   - "HTTP 에러율", "HTTP P99 응답시간"
2. **EC2 리소스** (SSH 접속 후)
   ```bash
   free -h                        # 스왑 사용량
   sudo docker stats --no-stream  # 컨테이너별 CPU/메모리
   ```
3. **앱 로그** (`traceId` 기준으로 특정 요청 추적 가능)
   ```bash
   sudo docker logs sashimi 2>&1 | grep "status=401"   # 인증 실패
   sudo docker logs sashimi --since <시각> --until <시각>
   ```
4. **nginx 접근 로그** (앱 로그에 안 남는 요청 — 예: Spring Security가 컨트롤러 도달 전에 막은 요청)
   ```bash
   sudo grep " 401 " /var/log/nginx/access.log | tail -30
   ```
   User-Agent가 `ModatScanner`, `zgrab`, `foda-scanner` 등이면 **인터넷 상시 스캔 봇 트래픽** —
   우리 서비스 문제 아님, 무시 가능.
5. **AWS CloudWatch** — EC2/RDS의 `CPUCreditBalance`, `CPUCreditUsage` (t3.micro/db.t3.micro는
   버스터블 인스턴스라 크레딧 고갈 시 성능 저하 가능. 2026-07-15 부하테스트에서는 크레딧
   문제는 없었음이 확인됨 — 최대치 288 유지)

---

## 7. 부하테스트

```bash
k6 run -e BASE_URL=https://api.sixsashimi.com.market-app.org \
  -e ADMIN_LOGIN_ID=admin01 -e ADMIN_LOGIN_PASSWORD=admin \
  k6/concurrent-users-capacity-test.js
```
- 점검모드를 켜놓고 돌리면 실사용자를 보호하면서 테스트 가능(관리자 계정 로그인 옵션으로
  우회, 위 3번 항목의 "계정 분리" 원칙 준수할 것)
- 테스트 전에 EC2 리소스 로깅을 미리 켜두면 사후 분석 가능:
  ```bash
  nohup bash -c 'while true; do echo "=== $(date) ==="; free -h; docker stats --no-stream; sleep 5; done' > ~/loadtest-memory.log 2>&1 &
  ```
- **2026-07-15 결과**: 동시 80 VU까지 에러율 0%. 병목은 CPU/메모리가 아니라 HikariCP 풀
  사이즈(기본값 10 방치)였음 — 20으로 조정 후 재검증에서 대기 큐 해소 확인 (CHANGELOG 참고).

---

## 8. 참고 링크
- Grafana: `http://<EC2 공인 IP>:3000` (대시보드: Sashimi Overview)
- Prometheus: 내부 IP 대역 또는 `ROLE_ADMIN`만 `/actuator/prometheus` 접근 가능
- 보안 점검 결과서: `SECURITY.md` 참고

# 로컬 개발 환경 설정 가이드

## 사전 준비

| 항목 | 버전 | 다운로드 |
|------|------|----------|
| Java (JDK) | 21 이상 | https://adoptium.net/ |
| Docker Desktop | 최신 | https://www.docker.com/products/docker-desktop/ |
| Git | 최신 | https://git-scm.com/ |
| IDE (권장) | IntelliJ IDEA | https://www.jetbrains.com/idea/ |

---

## 1. 프로젝트 클론

```bash
git clone <repository-url>
cd aitrip
```

---

## 2. Docker Desktop 실행

Docker Desktop을 설치한 후 **반드시 실행 상태**여야 합니다.

- Windows: 시작 메뉴에서 Docker Desktop 실행
- Mac: Applications에서 Docker Desktop 실행
- 트레이 아이콘이 초록색(Running)인지 확인

> Docker Desktop이 실행되지 않은 상태에서 `docker-compose`를 실행하면 연결 오류가 발생합니다.

---

## 3. MySQL + Redis 실행

프로젝트 루트에 `docker-compose.yml`이 있습니다. 터미널에서 아래 명령어를 실행하세요.

```bash
# 컨테이너 시작 (백그라운드)
docker-compose up -d
```

정상적으로 실행되면 아래와 같이 출력됩니다:

```
[+] Running 2/2
 ✔ Container aitrip-redis  Started
 ✔ Container aitrip-mysql   Started
```

### 확인 방법

```bash
# 컨테이너 상태 확인
docker-compose ps
```

| 컨테이너 | 포트 | 용도 |
|----------|------|------|
| aitrip-mysql | localhost:3306 | MySQL 8.0 데이터베이스 |
| aitrip-redis | localhost:6379 | Redis 7 (토큰 관리) |

### MySQL 접속 정보

| 항목 | 값 |
|------|------|
| Host | localhost |
| Port | 3306 |
| Database | aitrip |
| Username | root |
| Password | root |

### 자주 쓰는 Docker 명령어

```bash
# 컨테이너 중지
docker-compose down

# 컨테이너 중지 + 데이터 삭제 (DB 초기화)
docker-compose down -v

# 로그 확인
docker-compose logs -f mysql
docker-compose logs -f redis

# 컨테이너 재시작
docker-compose restart
```

---

## 4. 애플리케이션 실행

### 방법 1: 터미널

```bash
# Windows
gradlew.bat bootRun

# Mac / Linux
./gradlew bootRun
```

### 방법 2: IntelliJ IDEA

1. 프로젝트를 IntelliJ에서 열기 (Open → 프로젝트 폴더 선택)
2. Gradle 자동 import 완료 대기
3. `src/main/java/.../AitripApplication.java` 열기
4. `main` 메서드 옆 ▶ 버튼 클릭하여 실행

### 실행 확인

```
Started AitripApplication in X.XXX seconds
```

위 메시지가 나오면 정상 실행입니다.

---

## 5. API 문서 확인 (Swagger)

애플리케이션 실행 후 브라우저에서 접속:

```
http://localhost:8080/swagger-ui.html
```

Swagger UI에서 모든 API를 확인하고 테스트할 수 있습니다.

### JWT 인증이 필요한 API 테스트 방법

1. `/api/auth/signup`으로 회원가입
2. `/api/auth/login`으로 로그인 → 응답에서 `accessToken` 복사
3. Swagger UI 상단 **Authorize** 버튼 클릭
4. `Bearer {accessToken}` 입력 후 Authorize
5. 인증 필요 API 테스트 가능

---

## 6. 테스트 실행

테스트는 H2 인메모리 DB를 사용하므로 MySQL/Redis 없이도 실행 가능합니다.

```bash
# 전체 테스트
gradlew.bat test

# Mac / Linux
./gradlew test
```

---

## 트러블슈팅

### "Connection refused: localhost:3306"

MySQL 컨테이너가 실행 중인지 확인하세요.

```bash
docker-compose ps
```

컨테이너가 없으면 `docker-compose up -d`를 다시 실행하세요.

### "Port 8080 already in use"

이미 다른 프로세스가 8080 포트를 사용 중입니다.

```bash
# Windows - 포트 사용 중인 프로세스 찾기
netstat -ano | findstr :8080

# 해당 PID 종료
taskkill /PID {PID번호} /F
```

```bash
# Mac / Linux
lsof -i :8080
kill -9 {PID번호}
```

### Docker Desktop 관련 오류

```
error during connect: open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified.
```

→ Docker Desktop이 실행되지 않은 상태입니다. Docker Desktop을 먼저 실행하세요.

### "Port 3306 already in use" (Docker)

로컬에 MySQL이 이미 설치/실행 중인 경우 포트가 충돌합니다.

- **방법 1**: 로컬 MySQL 서비스 중지 후 Docker 사용
- **방법 2**: `docker-compose.yml`에서 포트를 변경 (예: `"3307:3306"`) 후 `application.yaml`의 datasource url도 수정

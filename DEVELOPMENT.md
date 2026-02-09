# AI Trip 개발 환경 가이드

팀원 공통 개발 환경 설정 가이드입니다.

---

## 목차

1. [로컬 개발 환경 구성](#1-로컬-개발-환경-구성)
2. [Mock 데이터 사용](#2-mock-데이터-사용)
3. [서비스 간 통신 구조](#3-서비스-간-통신-구조)
4. [API 엔드포인트](#4-api-엔드포인트)
5. [테스트 계정](#5-테스트-계정)
6. [트러블슈팅](#6-트러블슈팅)

---

## 1. 로컬 개발 환경 구성

### 사전 요구사항

| 도구 | 버전 | 필수 여부 |
|------|------|----------|
| Docker Desktop | 최신 | 필수 (모든 팀원) |
| Java 21 | 21+ | Backend 개발 시 |
| Python | 3.11+ | AI 개발 시 |
| Flutter SDK | 3.5+ | Frontend 개발 시 |

### Step 1: 저장소 클론

```bash
git clone <repository-url>
cd aitrip
```

### Step 2: 인프라 실행 (MySQL + Redis)

**모든 팀원 공통** - 본인이 담당하는 서비스와 관계없이 실행해주세요.

```bash
docker-compose up -d mysql redis
```

확인:
```bash
docker-compose ps
# mysql, redis가 healthy 상태인지 확인
```

### Step 3: 담당 서비스 실행

**Backend (Spring Boot):**
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```
- API 문서: http://localhost:8080/swagger-ui.html

**AI (FastAPI):**
```bash
cd ai
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```
- API 문서: http://localhost:8000/docs

**Frontend (Flutter):**
```bash
cd frontend
flutter pub get
flutter run -d chrome  # 웹으로 실행
```

---

## 2. Mock 데이터 사용

### Backend Mock 데이터

Backend를 `dev` 프로파일로 실행하면 `BaseInitData`가 자동으로 Mock 데이터를 생성합니다.

```bash
# Mock 데이터 포함 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# Windows
gradlew.bat bootRun --args='--spring.profiles.active=dev'

# Mock 데이터 없이 실행 (빈 DB)
./gradlew bootRun
```

### 포함된 Mock 데이터

| 데이터 | 내용 |
|--------|------|
| 유저 | 테스트 계정 3개 |
| 장소 | 서울/부산/제주 주요 관광지 17개 |

### Mock 데이터 추가하기

1. `backend/.../global/init/BaseInitData.java` 파일 수정
2. 새 메서드 추가 (예: `createNewPlaces()`)
3. PR 생성하여 팀원과 공유

```java
// 예시: 새 장소 추가
private void createNewPlaces() {
    placeRepository.save(Place.builder()
            .name("새 장소")
            .region("서울")
            .category("관광명소")
            .address("주소...")
            .build());
}
```

### 다른 팀원 변경사항 적용

```bash
git pull origin main
# 서버 재시작하면 자동 적용 (H2 create-drop)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

## 3. 서비스 간 통신 구조

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Frontend   │────▶│   Backend    │────▶│   AI Server  │
│  (Flutter)   │◀────│ (Spring Boot)│◀────│  (FastAPI)   │
│  :3000       │     │  :8080       │     │  :8000       │
└──────────────┘     └──────────────┘     └──────────────┘
                            │
                     ┌──────┴──────┐
                     ▼             ▼
               ┌──────────┐  ┌──────────┐
               │  MySQL   │  │  Redis   │
               │  :3306   │  │  :6379   │
               └──────────┘  └──────────┘
```

### 통신 흐름

1. **Frontend → Backend**: REST API 호출 (JWT 인증)
2. **Backend → AI**: 일정 추천 요청 (내부 HTTP 통신)
3. **Backend → MySQL**: 데이터 영속화
4. **Backend → Redis**: JWT 토큰 관리

### 각 서비스 Base URL

| 환경 | Backend | AI Server |
|------|---------|-----------|
| 로컬 개발 | http://localhost:8080 | http://localhost:8000 |
| Docker 내부 | http://backend:8080 | http://ai:8000 |

---

## 4. API 엔드포인트

### Backend API (Spring Boot)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| POST | `/api/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 | 불필요 |
| POST | `/api/auth/refresh` | 토큰 갱신 | 불필요 |
| POST | `/api/auth/logout` | 로그아웃 | 필요 |
| GET | `/api/users/me` | 내 정보 조회 | 필요 |
| GET | `/api/places` | 장소 목록 | 필요 |
| GET | `/api/places/{id}` | 장소 상세 | 필요 |
| POST | `/api/trips` | 여행 생성 | 필요 |
| GET | `/api/trips` | 내 여행 목록 | 필요 |

**상세 문서:** http://localhost:8080/swagger-ui.html

### AI API (FastAPI)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/health` | 헬스체크 |
| POST | `/api/schedule/generate` | AI 일정 생성 |
| POST | `/api/recommend/places` | 장소 추천 |

**상세 문서:** http://localhost:8000/docs

---

## 5. 테스트 계정

Backend `dev` 프로파일 실행 시 사용 가능한 계정입니다.

| 이메일 | 비밀번호 | 역할 | 용도 |
|--------|----------|------|------|
| admin@aitrip.com | Test1234! | 관리자 | 관리 기능 테스트 |
| user1@test.com | Test1234! | 일반 유저 | 일반 기능 테스트 |
| user2@test.com | Test1234! | 일반 유저 | 다중 유저 테스트 |

### 로그인 테스트 (cURL)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user1@test.com", "password": "Test1234!"}'
```

### 인증 토큰 사용

```bash
# 로그인 응답에서 accessToken 복사 후
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {accessToken}"
```

---

## 6. 트러블슈팅

### Docker 관련

**Q: MySQL 연결 실패**
```bash
# 컨테이너 상태 확인
docker-compose ps

# MySQL 로그 확인
docker-compose logs mysql

# 재시작
docker-compose down
docker-compose up -d mysql redis
```

**Q: 포트 충돌**
```bash
# 사용 중인 포트 확인 (Windows)
netstat -ano | findstr :3306
netstat -ano | findstr :8080

# 해당 프로세스 종료 후 재시도
```

### Backend 관련

**Q: Mock 데이터가 안 들어가요**
- `--spring.profiles.active=dev` 옵션 확인
- `ddl-auto: create-drop`이라 서버 재시작 시 데이터 초기화 (정상)
- 로그에서 `개발용 Mock 데이터 초기화 완료` 메시지 확인

**Q: Redis 연결 실패**
```bash
# Redis 컨테이너 확인
docker-compose ps redis

# Redis 직접 테스트
docker exec -it aitrip-redis redis-cli ping
# PONG 응답 확인
```

### AI 관련

**Q: Backend에서 AI 서버 연결 실패**
- AI 서버가 :8000에서 실행 중인지 확인
- `application.yaml`의 `ai.server.url` 설정 확인

---

## 추가 문서

- [Backend 개발 가이드](./backend/README.md)
- [Frontend 개발 가이드](./frontend/README.md)
- [AI 개발 가이드](./ai/README.md)

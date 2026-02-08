# 프론트엔드 API 연동 가이드

AI Trip 백엔드 API와 연동하기 위한 프론트엔드 팀 가이드입니다.

## 서버 정보

| 항목 | 값 |
|------|------|
| Base URL | `http://localhost:8080` |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| 인증 방식 | JWT Bearer Token |
| CORS 허용 | `localhost:3000`, `127.0.0.1:3000` |

---

## 테스트 계정

개발 환경(`dev` 프로파일)에서 사용 가능한 계정입니다.

| 이메일 | 비밀번호 | 역할 |
|--------|----------|------|
| admin@aitrip.com | Test1234! | ROLE_ADMIN |
| user1@test.com | Test1234! | ROLE_USER |
| user2@test.com | Test1234! | ROLE_USER |

---

## API 응답 형식

모든 API는 동일한 형식으로 응답합니다.

### 성공 응답

```json
{
  "code": "200",
  "message": "정상적으로 완료되었습니다.",
  "data": { ... }
}
```

### 에러 응답

```json
{
  "code": "404",
  "message": "존재하지 않는 여행입니다.",
  "data": null
}
```

### 유효성 검증 실패 (400)

```json
{
  "code": "400",
  "message": "잘못된 요청입니다.",
  "data": {
    "title": "여행 제목은 필수입니다.",
    "startDate": "출발일은 필수입니다."
  }
}
```

---

## 인증 방식

### JWT 토큰

- **Access Token**: 1시간 유효
- **Refresh Token**: 7일 유효

### 인증 흐름

```
1. POST /api/auth/login → accessToken, refreshToken 발급
2. API 호출 시 헤더에 토큰 포함: Authorization: Bearer {accessToken}
3. 401 에러 시 → POST /api/auth/refresh로 토큰 갱신
4. Refresh Token도 만료 시 → 재로그인
```

### 헤더 설정

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

---

## 주요 API 목록

### 인증 (토큰 불필요)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/refresh` | 토큰 갱신 |

### 인증 (토큰 필요)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/logout` | 로그아웃 |
| GET | `/api/users/me` | 내 정보 조회 |

### 여행 (토큰 필요, 본인 데이터만)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/trips` | 여행 생성 |
| GET | `/api/trips` | 내 여행 목록 |
| GET | `/api/trips/{id}` | 여행 상세 |
| PUT | `/api/trips/{id}` | 여행 수정 |
| DELETE | `/api/trips/{id}` | 여행 삭제 |
| PATCH | `/api/trips/{id}/status` | 상태 변경 |

### 장소 (조회는 토큰 불필요)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/places` | 장소 목록 |
| GET | `/api/places/{id}` | 장소 상세 |
| POST | `/api/places` | 장소 등록 (토큰 필요) |

### 일정 (토큰 필요)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/trips/{tripId}/schedules` | 일정 조회 |
| POST | `/api/trips/{tripId}/schedules` | 일정 생성 |
| PUT | `/api/schedules/{id}` | 일정 수정 |
| DELETE | `/api/trips/{tripId}/schedules` | 일정 전체 삭제 |

---

## 요청/응답 예시

### 회원가입

```bash
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password1!",
  "name": "홍길동"
}
```

**비밀번호 규칙:**
- 8~20자
- 대문자, 소문자, 숫자, 특수문자(@$!%*?&) 각 1개 이상

```javascript
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
```

### 로그인

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password1!"
}
```

응답:
```json
{
  "code": "200",
  "message": "정상적으로 완료되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 여행 생성

```bash
POST /api/trips
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "봄 여행",
  "region": "서울",
  "style": "감성적인 카페와 소품샵 위주",
  "tripStyle": "NORMAL",
  "startDate": "2025-03-10",
  "endDate": "2025-03-13"
}
```

---

## Enum 값

### TripStyle (여행 페이스)

| 값 | 설명 |
|------|------|
| `RELAXED` | 여유로운 |
| `NORMAL` | 보통 |
| `TIGHT` | 빡빡한 |

### TripStatus (여행 상태)

| 값 | 설명 |
|------|------|
| `DRAFT` | 초안 |
| `SCHEDULED` | 확정 |
| `COMPLETED` | 완료 |

---

## 에러 코드

### 인증 에러 (401)

| 코드 | 메시지 |
|------|--------|
| A001 | 유효하지 않은 토큰입니다. |
| A002 | 만료된 토큰입니다. |
| A005 | 인증이 필요합니다. |
| A006 | 로그아웃된 토큰입니다. |
| A007 | 유효하지 않은 리프레시 토큰입니다. |
| A008 | 이메일 또는 비밀번호가 올바르지 않습니다. |

### 사용자 에러

| 코드 | HTTP | 메시지 |
|------|------|--------|
| U001 | 409 | 이미 사용 중인 이메일입니다. |
| U002 | 404 | 존재하지 않는 사용자입니다. |

### 여행 에러

| 코드 | HTTP | 메시지 |
|------|------|--------|
| T001 | 404 | 존재하지 않는 여행입니다. |
| T002 | 403 | 해당 여행에 대한 권한이 없습니다. |

---

## 참고 문서

| 문서 | 설명 |
|------|------|
| [API_SPEC.md](./API_SPEC.md) | 전체 API 상세 명세 |
| [FRONTEND_AUTH_NEXTJS.md](./FRONTEND_AUTH_NEXTJS.md) | Next.js 인증 구현 예제 코드 |
| [ERD.md](./ERD.md) | 데이터베이스 구조 |

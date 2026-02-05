# AI Trip API 명세서

## 기본 정보

| 항목 | 값 |
|------|------|
| Base URL | `http://localhost:8080` |
| API 접두사 | `/api` |
| 인증 방식 | Bearer Token (JWT) |
| Swagger UI | http://localhost:8080/swagger-ui.html |

## 공통 응답 형식

모든 API는 아래 형식으로 응답합니다.

```json
{
  "code": "200",
  "message": "OK",
  "data": { ... }
}
```

### 에러 응답 예시

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

## 인증 방법

1. `/api/auth/login`으로 로그인하면 `accessToken`과 `refreshToken`을 받습니다.
2. 인증이 필요한 API 호출 시 Header에 토큰을 포함합니다.

```
Authorization: Bearer {accessToken}
```

3. accessToken 만료 시 `/api/auth/refresh`로 갱신합니다.

---

## 1. Auth (인증)

### POST `/api/auth/signup` - 회원가입

**인증: 불필요**

Request Body:
```json
{
  "email": "user@example.com",
  "password": "Password1!",
  "name": "홍길동"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 형식 |
| password | String | O | 대소문자+숫자+특수문자 각 1개 이상, 8~20자 |
| name | String | O | 이름 (최대 50자) |

Response (201):
```json
{
  "code": "201",
  "message": "CREATED",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "role": "ROLE_USER",
    "createdAt": "2025-03-10T12:00:00",
    "modifiedAt": "2025-03-10T12:00:00"
  }
}
```

---

### POST `/api/auth/login` - 로그인

**인증: 불필요**

Request Body:
```json
{
  "email": "user@example.com",
  "password": "Password1!"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 |
| password | String | O | 비밀번호 |

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

### POST `/api/auth/refresh` - 토큰 갱신

**인증: 불필요**

Request Body:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| refreshToken | String | O | 로그인 시 받은 Refresh Token |

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

### POST `/api/auth/logout` - 로그아웃

**인증: 필요** (`Authorization: Bearer {accessToken}`)

Request Body: 없음

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": null
}
```

---

## 2. User (사용자)

### GET `/api/users/me` - 내 정보 조회

**인증: 필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "role": "ROLE_USER",
    "createdAt": "2025-03-10T12:00:00",
    "modifiedAt": "2025-03-10T12:00:00"
  }
}
```

---

## 3. Trip (여행)

### POST `/api/trips` - 여행 생성

**인증: 필요**

Request Body:
```json
{
  "title": "봄 여행",
  "region": "서울",
  "style": "감성적인 카페와 소품샵 위주",
  "tripStyle": "NORMAL",
  "startDate": "2025-03-10",
  "endDate": "2025-03-13"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | String | O | 여행 제목 |
| region | String | O | 여행 지역 |
| style | String | X | 사용자가 입력한 여행 스타일 |
| tripStyle | String | O | 여행 페이스 (`RELAXED`, `NORMAL`, `TIGHT`) |
| startDate | String | O | 출발일 (yyyy-MM-dd) |
| endDate | String | O | 귀가일 (yyyy-MM-dd) |

Response (201):
```json
{
  "code": "201",
  "message": "CREATED",
  "data": {
    "id": 1,
    "userId": 1,
    "title": "봄 여행",
    "region": "서울",
    "style": "감성적인 카페와 소품샵 위주",
    "tripStyle": "NORMAL",
    "startDate": "2025-03-10",
    "endDate": "2025-03-13",
    "status": "DRAFT",
    "createdAt": "2025-03-10T12:00:00",
    "modifiedAt": "2025-03-10T12:00:00"
  }
}
```

---

### GET `/api/trips` - 내 여행 목록 조회

**인증: 필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "title": "봄 여행",
      "region": "서울",
      "style": "감성적인 카페와 소품샵 위주",
      "tripStyle": "NORMAL",
      "startDate": "2025-03-10",
      "endDate": "2025-03-13",
      "status": "DRAFT",
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### GET `/api/trips/{id}` - 여행 상세 조회

**인증: 필요** (본인 여행만 조회 가능)

Response (200): Trip 객체 (위와 동일)

---

### PUT `/api/trips/{id}` - 여행 수정

**인증: 필요** (본인 여행만 수정 가능)

Request Body: 여행 생성과 동일

Response (200): 수정된 Trip 객체

---

### DELETE `/api/trips/{id}` - 여행 삭제

**인증: 필요** (본인 여행만 삭제 가능)

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": null
}
```

---

### PATCH `/api/trips/{id}/status` - 여행 상태 변경

**인증: 필요** (본인 여행만 변경 가능)

Request Body:
```json
{
  "status": "SCHEDULED"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| status | String | O | `DRAFT`, `SCHEDULED`, `COMPLETED` |

Response (200): 수정된 Trip 객체

---

## 4. Place (장소)

### GET `/api/places` - 장소 목록 조회

**인증: 불필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "name": "블루보틀",
      "region": "서울",
      "category": "카페",
      "address": "강남구 압구정로 27",
      "latitude": 37.5265,
      "longitude": 127.0402,
      "operatingHours": "09:00-21:00",
      "estimatedStayTime": 45,
      "imageUrl": "https://example.com/image.jpg",
      "source": "TourAPI",
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### GET `/api/places/{id}` - 장소 상세 조회

**인증: 불필요**

Response (200): Place 객체 (위와 동일)

---

### POST `/api/places` - 장소 등록

**인증: 필요**

Request Body:
```json
{
  "name": "블루보틀",
  "region": "서울",
  "category": "카페",
  "address": "강남구 압구정로 27",
  "latitude": 37.5265,
  "longitude": 127.0402,
  "operatingHours": "09:00-21:00",
  "estimatedStayTime": 45,
  "imageUrl": "https://example.com/image.jpg",
  "source": "TourAPI"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | String | O | 장소명 |
| region | String | O | 지역 |
| category | String | O | 카테고리 |
| address | String | O | 주소 |
| latitude | BigDecimal | X | 위도 |
| longitude | BigDecimal | X | 경도 |
| operatingHours | String | X | 영업시간 |
| estimatedStayTime | Integer | X | 권장 체류시간 (분) |
| imageUrl | String | X | 이미지 URL |
| source | String | X | 데이터 출처 |

Response (201): Place 객체

---

### PUT `/api/places/{id}` - 장소 수정

**인증: 필요**

Request Body: 장소 등록과 동일

Response (200): 수정된 Place 객체

---

### DELETE `/api/places/{id}` - 장소 삭제

**인증: 필요**

Response (200): `"data": null`

---

## 5. PlaceStyleTag (장소 스타일 태그)

### GET `/api/places/{placeId}/tags` - 장소 태그 조회

**인증: 불필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "placeId": 1,
      "tag": "감성",
      "frequency": 15,
      "weight": 0.85,
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### POST `/api/places/{placeId}/tags` - 태그 등록

**인증: 필요**

Request Body:
```json
{
  "tag": "감성",
  "frequency": 15,
  "weight": 0.85
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| tag | String | O | 태그명 |
| frequency | Integer | X | 언급 횟수 |
| weight | BigDecimal | X | 중요도 (0~1) |

Response (201): PlaceStyleTag 객체

---

## 6. PlaceMovingTime (장소 간 이동시간)

### GET `/api/place-moving-times` - 이동시간 조회

**인증: 불필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "fromPlaceId": 1,
      "toPlaceId": 2,
      "distanceKm": 3.5,
      "timeMinutes": 15,
      "transportType": "도보",
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### POST `/api/place-moving-times` - 이동시간 등록

**인증: 필요**

Request Body:
```json
{
  "fromPlaceId": 1,
  "toPlaceId": 2,
  "distanceKm": 3.5,
  "timeMinutes": 15,
  "transportType": "도보"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| fromPlaceId | Long | O | 출발 장소 ID |
| toPlaceId | Long | O | 도착 장소 ID |
| distanceKm | BigDecimal | X | 거리 (km) |
| timeMinutes | Integer | X | 이동시간 (분) |
| transportType | String | X | 이동수단 (도보, 대중교통, 자동차 등) |

Response (201): PlaceMovingTime 객체

---

## 7. PlaceCrowdData (장소 혼잡도)

### GET `/api/place-crowd-data` - 혼잡도 조회

**인증: 불필요**

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "placeId": 1,
      "dayOfWeek": 1,
      "hour": 12,
      "crowdLevel": "높음",
      "waitingRiskScore": 0.75,
      "avgWaitingMin": 30,
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### POST `/api/place-crowd-data` - 혼잡도 등록

**인증: 필요**

Request Body:
```json
{
  "placeId": 1,
  "dayOfWeek": 1,
  "hour": 12,
  "crowdLevel": "높음",
  "waitingRiskScore": 0.75,
  "avgWaitingMin": 30
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| placeId | Long | O | 장소 ID |
| dayOfWeek | Integer | O | 요일 (0=일, 1=월, ..., 6=토) |
| hour | Integer | O | 시간 (0~23) |
| crowdLevel | String | X | 혼잡도 (낮음, 중간, 높음) |
| waitingRiskScore | BigDecimal | X | 대기 위험도 (0~1) |
| avgWaitingMin | Integer | X | 평균 대기시간 (분) |

Response (201): PlaceCrowdData 객체

---

## 8. TripPlace (여행 장소)

### GET `/api/trips/{tripId}/places` - 여행 장소 목록 조회

**인증: 필요** (본인 여행만)

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "tripId": 1,
      "placeId": 1,
      "placeName": "블루보틀",
      "isSelected": true,
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### POST `/api/trips/{tripId}/places` - 여행에 장소 추가

**인증: 필요** (본인 여행만)

Request Body:
```json
{
  "placeId": 1,
  "isSelected": true
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| placeId | Long | O | 장소 ID |
| isSelected | Boolean | X | 선택 여부 |

Response (201): TripPlace 객체

---

### DELETE `/api/trips/{tripId}/places/{placeId}` - 여행에서 장소 제거

**인증: 필요** (본인 여행만)

Response (200): `"data": null`

---

## 9. Schedule (일정)

### GET `/api/trips/{tripId}/schedules` - 여행 일정 조회

**인증: 필요** (본인 여행만)

Response (200):
```json
{
  "code": "200",
  "message": "OK",
  "data": [
    {
      "id": 1,
      "tripId": 1,
      "placeId": 1,
      "placeName": "블루보틀",
      "dayNumber": 1,
      "visitOrder": 1,
      "startTime": "10:00:00",
      "endTime": "11:30:00",
      "estimatedWaitingTime": 30,
      "travelTimeFromPrev": 15,
      "stayDuration": 45,
      "notes": "아메리카노 추천",
      "createdAt": "2025-03-10T12:00:00",
      "modifiedAt": "2025-03-10T12:00:00"
    }
  ]
}
```

---

### POST `/api/trips/{tripId}/schedules` - 일정 생성

**인증: 필요** (본인 여행만)

Request Body:
```json
{
  "placeId": 1,
  "dayNumber": 1,
  "visitOrder": 1,
  "startTime": "10:00",
  "endTime": "11:30",
  "estimatedWaitingTime": 30,
  "travelTimeFromPrev": 15,
  "stayDuration": 45,
  "notes": "아메리카노 추천"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| placeId | Long | O | 장소 ID |
| dayNumber | Integer | O | 일차 (1일차, 2일차...) |
| visitOrder | Integer | O | 해당 일차 내 방문 순서 |
| startTime | String | X | 시작 시간 (HH:mm) |
| endTime | String | X | 종료 시간 (HH:mm) |
| estimatedWaitingTime | Integer | X | 예상 대기시간 (분) |
| travelTimeFromPrev | Integer | X | 이전 장소로부터 이동시간 (분) |
| stayDuration | Integer | X | 체류 시간 (분) |
| notes | String | X | 메모 |

Response (201): Schedule 객체

---

### PUT `/api/schedules/{id}` - 일정 수정

**인증: 필요**

Request Body: 일정 생성과 동일

Response (200): 수정된 Schedule 객체

---

### DELETE `/api/trips/{tripId}/schedules` - 여행 일정 전체 삭제

**인증: 필요** (본인 여행만)

Response (200): `"data": null`

---

## 에러 코드 목록

### 인증 (Auth)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| A001 | 401 | 유효하지 않은 토큰입니다. |
| A002 | 401 | 만료된 토큰입니다. |
| A003 | 401 | 지원하지 않는 토큰 형식입니다. |
| A004 | 401 | 토큰이 비어있습니다. |
| A005 | 401 | 인증이 필요합니다. |
| A006 | 401 | 로그아웃된 토큰입니다. |
| A007 | 401 | 유효하지 않은 리프레시 토큰입니다. |
| A008 | 401 | 이메일 또는 비밀번호가 올바르지 않습니다. |

### 사용자 (User)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| U001 | 409 | 이미 사용 중인 이메일입니다. |
| U002 | 404 | 존재하지 않는 사용자입니다. |
| U003 | 401 | 비밀번호가 일치하지 않습니다. |

### 여행 (Trip)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| T001 | 404 | 존재하지 않는 여행입니다. |
| T002 | 403 | 해당 여행에 대한 권한이 없습니다. |

### 장소 (Place)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| P001 | 404 | 존재하지 않는 장소입니다. |

### 장소 스타일 태그 (PlaceStyleTag)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| PS001 | 404 | 존재하지 않는 장소 스타일 태그입니다. |

### 장소 이동시간 (PlaceMovingTime)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| PM001 | 404 | 존재하지 않는 이동시간 정보입니다. |

### 장소 혼잡도 (PlaceCrowdData)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| PC001 | 404 | 존재하지 않는 혼잡도 정보입니다. |

### 여행 장소 (TripPlace)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| TP001 | 404 | 존재하지 않는 여행 장소입니다. |
| TP002 | 409 | 이미 여행에 추가된 장소입니다. |

### 일정 (Schedule)

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| S001 | 404 | 존재하지 않는 일정입니다. |

---

## Enum 값 정리

### TripStyle (여행 페이스)

| 값 | 설명 |
|------|------|
| `RELAXED` | 여유로운 |
| `NORMAL` | 보통 |
| `TIGHT` | 빡빡한 |

### TripStatus (여행 상태)

| 값 | 설명 |
|------|------|
| `DRAFT` | 초안 (생성 직후 기본값) |
| `SCHEDULED` | 확정 |
| `COMPLETED` | 완료 |

### User.Role (사용자 역할)

| 값 | 설명 |
|------|------|
| `ROLE_USER` | 일반 사용자 |
| `ROLE_ADMIN` | 관리자 |

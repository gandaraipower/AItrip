# AI 서버 연동 가이드

Spring Boot 백엔드와 AI 서버(FastAPI/Node.js) 간의 통신 규약입니다.

## 개요

```
[Frontend] → [Spring Boot API] → [AI Server (FastAPI)]
                    ↓
              일정 추천 요청/응답
```

Spring Boot가 AI 서버를 호출하여 일정을 생성합니다. AI 서버는 추천 알고리즘만 담당합니다.

---

## AI 서버가 제공해야 할 API

### 1. 일정 생성 API

```
POST /api/schedule/generate
Content-Type: application/json
```

#### 요청 (Spring → AI)

```json
{
  "region": "서울",
  "startDate": "2025-03-10",
  "endDate": "2025-03-13",
  "tripStyle": "NORMAL",
  "preferences": ["카페", "감성", "사진맛집"]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| region | String | 여행 지역 |
| startDate | String | 출발일 (yyyy-MM-dd) |
| endDate | String | 귀가일 (yyyy-MM-dd) |
| tripStyle | String | 여행 페이스 (`RELAXED`, `NORMAL`, `TIGHT`) |
| preferences | String[] | 사용자 선호 키워드 |

#### 응답 (AI → Spring)

```json
{
  "success": true,
  "message": "일정 생성 완료",
  "recommendations": [
    {
      "placeName": "블루보틀 삼청",
      "category": "카페",
      "visitOrder": 1,
      "dayNumber": 1,
      "estimatedMinutes": 45,
      "reason": "감성 키워드 매칭, 오전 혼잡도 낮음"
    },
    {
      "placeName": "북촌한옥마을",
      "category": "관광",
      "visitOrder": 2,
      "dayNumber": 1,
      "estimatedMinutes": 90,
      "reason": "사진맛집 키워드 매칭"
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 |
| message | String | 결과 메시지 |
| recommendations | Array | 추천 장소 목록 |
| └ placeName | String | 장소명 |
| └ category | String | 카테고리 |
| └ visitOrder | Integer | 해당 일차 내 방문 순서 |
| └ dayNumber | Integer | 일차 (1일차, 2일차...) |
| └ estimatedMinutes | Integer | 권장 체류 시간 (분) |
| └ reason | String | 추천 이유 (사용자에게 표시) |

---

### 2. 헬스체크 API

```
GET /health
```

응답:
```json
{
  "status": "ok"
}
```

또는 HTTP 200 OK (body 없음)

---

## 에러 응답 형식

AI 서버 에러 시:

```json
{
  "success": false,
  "message": "추천 가능한 장소가 없습니다.",
  "recommendations": []
}
```

Spring Boot는 이 응답을 받아 사용자에게 적절한 에러 메시지를 반환합니다.

---

## 타임아웃 설정

| 항목 | 값 |
|------|------|
| 연결 타임아웃 | 5초 |
| 응답 타임아웃 | 30초 |

AI 서버는 30초 내에 응답해야 합니다. 초과 시 Spring Boot에서 타임아웃 에러를 반환합니다.

---

## TripStyle 값 설명

AI 서버에서 일정 밀도를 조절할 때 참고하세요.

| 값 | 설명 | 일정 밀도 |
|------|------|------|
| `RELAXED` | 여유로운 | 하루 2-3곳 |
| `NORMAL` | 보통 | 하루 3-4곳 |
| `TIGHT` | 빡빡한 | 하루 5곳 이상 |

---

## 데이터 활용

Spring Boot DB에 저장된 데이터를 AI 서버에서 활용할 수 있습니다.

### 장소 정보 API

```
GET /api/places
```

응답:
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
      "estimatedStayTime": 45
    }
  ]
}
```

### 장소 스타일 태그

```
GET /api/places/{placeId}/tags
```

```json
{
  "code": "200",
  "data": [
    {
      "tag": "감성",
      "frequency": 15,
      "weight": 0.85
    }
  ]
}
```

### 장소 간 이동시간

```
GET /api/place-moving-times
```

```json
{
  "code": "200",
  "data": [
    {
      "fromPlaceId": 1,
      "toPlaceId": 2,
      "distanceKm": 3.5,
      "timeMinutes": 15,
      "transportType": "도보"
    }
  ]
}
```

### 장소 혼잡도

```
GET /api/place-crowd-data
```

```json
{
  "code": "200",
  "data": [
    {
      "placeId": 1,
      "dayOfWeek": 1,
      "hour": 12,
      "crowdLevel": "높음",
      "avgWaitingMin": 30
    }
  ]
}
```

---

## 개발 환경 설정

### AI 서버 URL 설정

Spring Boot `application.yaml`:

```yaml
ai:
  server:
    url: ${AI_SERVER_URL:http://localhost:8000}
    timeout: 30000
```

환경변수로 설정:
```bash
export AI_SERVER_URL=http://localhost:8000
```

### FastAPI 서버 예시

```python
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI()

class ScheduleRequest(BaseModel):
    region: str
    startDate: str
    endDate: str
    tripStyle: str
    preferences: List[str]

class PlaceRecommendation(BaseModel):
    placeName: str
    category: str
    visitOrder: int
    dayNumber: int
    estimatedMinutes: int
    reason: str

class ScheduleResponse(BaseModel):
    success: bool
    message: str
    recommendations: List[PlaceRecommendation]

@app.get("/health")
def health_check():
    return {"status": "ok"}

@app.post("/api/schedule/generate", response_model=ScheduleResponse)
def generate_schedule(request: ScheduleRequest):
    # TODO: AI 추천 로직 구현
    recommendations = [
        PlaceRecommendation(
            placeName="블루보틀 삼청",
            category="카페",
            visitOrder=1,
            dayNumber=1,
            estimatedMinutes=45,
            reason="감성 키워드 매칭"
        )
    ]
    return ScheduleResponse(
        success=True,
        message="일정 생성 완료",
        recommendations=recommendations
    )
```

실행:
```bash
uvicorn main:app --reload --port 8000
```

---

## 체크리스트

AI 서버 개발 시 확인사항:

- [ ] `GET /health` 엔드포인트 구현
- [ ] `POST /api/schedule/generate` 엔드포인트 구현
- [ ] 요청/응답 JSON 형식 준수
- [ ] 30초 내 응답 가능 확인
- [ ] CORS 설정 (Spring Boot에서 호출하므로 불필요할 수 있음)
- [ ] 에러 시 `success: false` 응답 반환

---

## 참고 문서

| 문서 | 설명 |
|------|------|
| [API_SPEC.md](./API_SPEC.md) | Spring Boot API 전체 명세 |
| [ERD.md](./ERD.md) | 데이터베이스 구조 |
| [SKILL.md](./skills/spring-api-rules/SKILL.md) | Spring Boot 개발 규칙 (13장 참고) |

# 📊 AI 여행 일정 생성 앱 - ERD

## 🔄 ERD (관계도)

```
┌─────────────┐
│    User     │  회원가입/로그인 (Role 포함)
└──────┬──────┘
       │ 1:N
       │
┌──────▼──────────────────┐
│       Trip              │  여행 기본 정보 + 상태
└──────┬──────────────────┘
       │ 1:N (한 여행에 여러 일정)
       │
┌──────▼──────────────────┐
│      Schedule           │  AI가 최적화한 일정
├──────────────────────────┤
│ 각 일정은:               │
│ - Day 1,2,3,4... (일차) │
│ - 방문 순서              │
│ - 시간 (출발/도착)      │
│ - 대기/체류/이동시간    │
└──────┬──────────────────┘
       │ N:1 (많은 일정이 장소 참조)
       │
┌──────▼──────────────────┐
│       Place             │  공공 API 장소 데이터
└──────┬──────────────────┘
       │ 1:N (한 장소는 여러 일정/여행에서 사용)
       │
    ┌──┴──┐
    │     │
    │  ┌──▼──────────────────────┐
    │  │  PlaceStyleTag          │  스타일 태그
    │  └─────────────────────────┘
    │
    │  ┌──────────────────────────┐
    │  │ PlaceMovingTime          │  이동 시간
    │  │ (from_place ↔ to_place)  │
    │  └─────────────────────────┘
    │
    └──┐
       │
    ┌──▼──────────────────────┐
    │ PlaceCrowdData          │  혼잡도/웨이팅
    └─────────────────────────┘

추가 관계:
┌─────────────────┐
│   TripPlace     │  여행에 포함된 장소들
├─────────────────┤
│ Trip (N:1)      │
│ Place (N:1)     │
└─────────────────┘

```

---

## 📋 테이블 상세 정의

### 1️⃣ User (사용자)

```
┌────────────────────────────┐
│        User                │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ email (UNIQUE)    VARCHAR  │  로그인용
│ password (HASHED) VARCHAR  │  BCrypt 암호화
│ name               VARCHAR │  사용자명
│ role               VARCHAR │  ROLE_ADMIN / ROLE_USER
└────────────────────────────┘

```

---

### 2️⃣ Trip (여행)

```
┌────────────────────────────┐
│        Trip                │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ user_id (FK)         BIGINT│  어느 사용자의 여행
│ title             VARCHAR  │  여행 제목
│ region            VARCHAR  │  여행지역
│ style               TEXT   │  사용자 입력 스타일
│ trip_style           ENUM  │  여유/보통/빡빡
│ start_date           DATE  │
│ end_date             DATE  │
│ status               ENUM  │  draft/scheduled/completed
└────────────────────────────┘

상태 설명:
- draft: 사용자가 기본정보 입력만 함 (Phase 2)
- scheduled: 최종 일정 확정됨 (Phase 6)
- completed: 여행 완료

```

---

### 3️⃣ Place (공공 장소 데이터)

```
┌────────────────────────────┐
│        Place               │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ name              VARCHAR  │
│ region            VARCHAR  │
│ category          VARCHAR  │  관광지/맛집/카페/소품샵
│ address           VARCHAR  │
│ latitude          DECIMAL  │  지도 위치
│ longitude         DECIMAL  │
│ operating_hours   VARCHAR  │
│ estimated_stay_time INT    │  권장 체류시간(분)
│ image_url         VARCHAR  │
│ source            VARCHAR  │  TourAPI/Kakao/Naver
└────────────────────────────┘

용도:
- Mode A: AI 추천에 사용
- Mode B: 지도 마커 표시에 사용

```

---

### 4️⃣ PlaceStyleTag (장소별 스타일 태그 - AI 학습용)

```
┌────────────────────────────┐
│    PlaceStyleTag           │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ place_id (FK)        BIGINT│
│ tag                VARCHAR  │  감성, 웨이팅, 조용함, 사진 등
│ frequency            INT   │  해당 태그 언급 횟수
│ weight              DECIMAL │  0~1 (중요도)
└────────────────────────────┘

용도:
- Mode A: AI가 감성순으로 장소 추천
- Mode B: 지도 마커를 감성순으로 필터링

```

---

### 5️⃣ PlaceMovingTime (장소 간 이동 시간 - AI 학습용)

```
┌────────────────────────────┐
│   PlaceMovingTime          │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ from_place_id (FK)   BIGINT│
│ to_place_id (FK)     BIGINT│
│ distance_km         DECIMAL │  거리(km)
│ time_minutes            INT│  예상 이동 시간(분)
│ transport_type      VARCHAR│  도보/택시/대중교통
└────────────────────────────┘

UNIQUE: (from_place_id, to_place_id)

용도:
- AI가 Schedule 생성 시 이동 시간 계산
- TSP 알고리즘에 입력

```

---

### 6️⃣ PlaceCrowdData (시간대별 혼잡도/웨이팅 - AI 학습용)

```
┌────────────────────────────┐
│    PlaceCrowdData          │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ place_id (FK)        BIGINT│
│ day_of_week            INT │  0~6 (일~토)
│ hour                   INT │  0~23
│ crowd_level         VARCHAR│  낮음/중간/높음
│ waiting_risk_score DECIMAL │  0~1 (대기 위험도)
│ avg_waiting_min        INT │  평균 대기시간(분)
└────────────────────────────┘

UNIQUE: (place_id, day_of_week, hour)

용도:
- AI가 Schedule 생성 시 웨이팅 시간 포함
- 시간대별 최적 방문 순서 결정

```

---

### 7️⃣ TripPlace (여행-장소 관계)

```
┌────────────────────────────┐
│      TripPlace             │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ trip_id (FK)         BIGINT│
│ place_id (FK)        BIGINT│
│ is_selected          BOOLEAN  true: 방문 예정
└────────────────────────────┘

UNIQUE: (trip_id, place_id)

생성 시점:
- Mode A: Phase 3A (AI 자동 생성)
- Mode B: Phase 4B (사용자 선택 완료 시)

생명주기:
- 사용자가 제거/추가하면 즉시 수정

```

---

### 8️⃣ Schedule (최종 일정)

```
┌────────────────────────────┐
│      Schedule              │
├────────────────────────────┤
│ id (PK)              BIGINT│
│ trip_id (FK)         BIGINT│  ← Trip과 1:N 관계
│ place_id (FK)        BIGINT│  ← Place와 N:1 관계
│ day_number            INT  │  1일차, 2일차, ...
│ visit_order           INT  │  순서 (1, 2, 3...)
│ start_time            TIME │
│ end_time              TIME │
│ estimated_waiting_time INT │  AI 계산
│ travel_time_from_prev INT  │  이동 시간(분)
│ stay_duration         INT  │  체류 시간(분)
│ notes                 TEXT │  메모
└────────────────────────────┘

INDEX: (trip_id, day_number)

관계 설명:
┌────────────────┐
│     Trip (1)   │
│   trip_id=1    │
│   "봄 여행"    │
└────────┬───────┘
         │ 1:N (한 여행에 여러 일정)
         │
    ┌────▼──────────────────────────┐
    │ Schedule ×18개 (모두 trip_id=1) │
    ├────────────────────────────────┤
    │ Schedule 1 (day=1, place=102)  │
    │ Schedule 2 (day=1, place=101)  │
    │ Schedule 3 (day=1, place=104)  │
    │ Schedule 4 (day=2, place=103)  │
    │ ...                            │
    │ Schedule 18 (day=4, place=105) │
    └────┬──────────────────────────┘
         │ N:1 (많은 일정이 장소 참조)
         │
    ┌────▼──────────┐
    │ Place (N)     │
    │ place_id=102  │
    │ place_id=101  │
    │ place_id=104  │
    │ ... 등등      │
    └───────────────┘

같은 장소가 여러 일정에서 사용될 수 있음:
- 예: 블루보틀(place_id=102)는 Day1, Day2, Day3에서 각각 1회씩 방문 가능
- 하지만 보통은 한 여행에서 같은 장소를 여러 번 방문하지는 않음

```

생성 시점:

- Phase 5A (Mode A) / Phase 4B (Mode B): AI가 최적 루트 계산 후
- TSP 알고리즘으로 최적 순서 결정됨

표시되는 정보 (Phase 6A/5B):

- 출발 시간, 도착 시간
- 대기 시간 (PlaceCrowdData 참고)
- 체류 시간 (Place.estimated_stay_time 참고)
- 이동 시간 (PlaceMovingTime 참고)

```

---

## 🎯 데이터 흐름

### Mode A: AI 추천 → 수정 → 일정 생성

```

Phase 3A: TripPlace 자동 생성
├─ PlaceStyleTag 데이터로 감성순 추천
└─ 18개 장소 INSERT (trip_id=1)

Phase 4A: 사용자 수정 (메모리)
├─ 제거: DELETE FROM trip_place
└─ 추가: INSERT INTO trip_place

Phase 5A: AI가 최적 루트 계산
├─ PlaceMovingTime 조회 (이동시간)
├─ PlaceCrowdData 조회 (웨이팅)
├─ TSP 알고리즘 실행
└─ 18개 Schedule INSERT (최적화된 순서)

Phase 6A: 확인 & 최종 수정
├─ 시간표 표시 (Schedule 데이터)
├─ 사용자 수정 가능
└─ Trip.status = 'scheduled'

```

### Mode B: 지도 선택 → 일정 생성

```

Phase 3B: 지도에서 선택 (메모리만)
├─ Place 데이터를 지도에 마커로 표시
└─ PlaceStyleTag로 감성순 필터링

Phase 4B: 선택 완료 → TripPlace 생성
└─ 선택한 15개 INSERT INTO trip_place

Phase 5B: AI가 최적 루트 계산
├─ PlaceMovingTime 조회
├─ PlaceCrowdData 조회
├─ TSP 알고리즘 실행
└─ 18개 Schedule INSERT

Phase 6B: 확인 & 최종 수정 (Mode A와 동일)
├─ 시간표 표시
├─ 사용자 수정 가능
└─ Trip.status = 'scheduled'

```

---

## 💾 DB 저장 시점 정리

| Phase | Mode A | Mode B | SQL |
|-------|--------|--------|-----|
| **2** | ✅ | ✅ | INSERT INTO trip |
| **3** | ✅ Auto | ❌ | INSERT INTO trip_place (자동) |
| **4** | ✅ Manual | ✅ Manual | INSERT/DELETE trip_place |
| **5** | ✅ Auto | ✅ Auto | INSERT INTO schedule (TSP) |
| **6** | ✅ | ✅ | UPDATE trip SET status='scheduled' |

---

## 🔐 Role 정보

| Role | 설명 |
|------|------|
| **ROLE_ADMIN** | 관리자 (Place, PlaceStyleTag, PlaceMovingTime, PlaceCrowdData 관리) |
| **ROLE_USER** | 일반 사용자 (자신의 여행만 CRUD) |

---

## 📊 각 테이블의 역할

| 테이블 | 주 용도 | 수정 주체 | 비고 |
|--------|--------|---------|------|
| **User** | 인증 | 사용자 | 회원가입/로그인 |
| **Trip** | 여행 관리 | 사용자 | 기본정보, 상태 관리 |
| **Place** | 공공 데이터 | 관리자 | TourAPI/Kakao에서 수집 |
| **PlaceStyleTag** | AI 추천 | AI + 관리자 | NLP로 태그 생성 |
| **PlaceMovingTime** | AI 최적화 | 배치 작업 | 지도 API로 계산 |
| **PlaceCrowdData** | AI 최적화 | 배치 작업 | 리뷰 분석으로 생성 |
| **TripPlace** | 여행 구성 | 사용자 + AI | Mode A는 자동, Mode B는 수동 |
| **Schedule** | 최종 일정 | AI | TSP 알고리즘으로 생성 |

---

## 🎨 UI에서 표시되는 데이터

### Phase 4A/4B (추천 장소 & 최종 루트)

```

🗺️ 지도:
└─ Place.latitude, longitude로 마커 표시

📋 리스트:
├─ Place.name, category
├─ PlaceStyleTag.tag, weight (감성도)
├─ Place.estimated_stay_time (체류)
└─ PlaceCrowdData.avg_waiting_min (대기)

```

### Phase 6A/6B (최종 일정 확인)

```

🗺️ 지도:
└─ Schedule.visit_order로 루트 시각화

📋 시간표:
├─ Schedule.start_time, end_time
├─ PlaceCrowdData.avg_waiting_min (대기)
├─ Place.estimated_stay_time (체류)
├─ PlaceMovingTime.time_minutes (이동)
└─ Place.address (위치)

```

---

## 🚀 초기화 순서

```

1. Place 테이블 채우기 (TourAPI + Kakao)
   └─ 기본 장소 정보
2. PlaceStyleTag 테이블 채우기
   └─ NLP로 태그 생성 + 가중치 계산
3. PlaceMovingTime 테이블 채우기
   └─ 지도 API로 이동시간 계산
4. PlaceCrowdData 테이블 채우기
   └─ 리뷰 분석으로 혼잡도 계산
5. User, Trip, TripPlace, Schedule
   └─ 사용자가 앱 사용하면서 동적 생성

```

---

## 📌 최종 요약

**8개 테이블:**
1. **User** - 사용자
2. **Trip** - 여행 (기본정보 + 상태)
3. **Place** - 공공 장소
4. **PlaceStyleTag** - 스타일 태그 (AI 학습용)
5. **PlaceMovingTime** - 이동 시간 (AI 학습용)
6. **PlaceCrowdData** - 혼잡도/웨이팅 (AI 학습용)
7. **TripPlace** - 여행-장소 관계
8. **Schedule** - 최종 일정 (AI 최적화)

**특징:**
- ✅ Mode A, B 모두 지원
- ✅ AI 추천 기능 지원
- ✅ 최적 루트 자동 생성
- ✅ 시간정보 (대기, 체류, 이동) 포함
- ✅ 유연한 수정 기능

**완성! 이제 정말 완벽한 ERD!** ✅

```
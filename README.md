# AI Trip

AI 기반 여행 추천 서비스 모노레포입니다.

## 프로젝트 구조

```
aitrip/
├── backend/          # Spring Boot REST API
├── frontend/         # Next.js 웹 앱
├── ai/               # FastAPI AI 추천 서비스
├── docker-compose.yml       # 로컬 개발용
├── docker-compose.prod.yml  # EC2 운영용
└── README.md
```

## 기술 스택

| 서비스 | 기술 | 포트 |
|--------|------|------|
| Backend | Spring Boot 3, Java 21 | 8080 |
| Frontend | Next.js, Node 20 | 3000 |
| AI | FastAPI, Python 3.11 | 8000 |
| Database | MySQL 8.0 | 3306 |
| Cache | Redis 7 | 6379 |

## 시작하기

### 사전 요구사항

- Docker & Docker Compose
- Java 21 (Backend 개발 시)
- Node.js 20+ (Frontend 개발 시)
- Python 3.11+ (AI 개발 시)

### Docker로 전체 실행

```bash
docker-compose up -d
```

### 개별 서비스 실행

**Backend:**
```bash
cd backend
./gradlew bootRun
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

**AI:**
```bash
cd ai
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## API 문서

- Backend: http://localhost:8080/swagger-ui.html
- AI: http://localhost:8000/docs

## 문서

| 문서 | 설명 |
|------|------|
| [DEVELOPMENT.md](./DEVELOPMENT.md) | 로컬 개발 환경 설정 (필독) |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | AWS EC2 배포 가이드 |

### 서비스별 가이드

- [Backend README](./backend/README.md)
- [Frontend README](./frontend/README.md)
- [AI README](./ai/README.md)

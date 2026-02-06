# AI Trip AI Service (FastAPI)

AI 기반 여행 추천 서비스입니다.

## 요구사항

- Python 3.11+
- pip

## 설치 및 실행

```bash
# 가상환경 생성 (권장)
python -m venv venv
source venv/bin/activate  # Linux/Mac
# venv\Scripts\activate   # Windows

# 의존성 설치
pip install -r requirements.txt

# 환경 변수 설정
cp .env.example .env
# .env 파일을 편집하여 필요한 값 설정

# 개발 서버 실행
uvicorn app.main:app --reload --port 8000
```

## API 문서

서버 실행 후:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 프로젝트 구조

```
app/
├── main.py              # FastAPI 앱 진입점
├── core/
│   └── config.py        # 설정
├── api/
│   └── v1/
│       ├── router.py    # API 라우터
│       └── endpoints/   # 엔드포인트
├── models/              # Pydantic 모델
└── services/            # 비즈니스 로직
tests/                   # 테스트
```

## 테스트

```bash
pytest
```

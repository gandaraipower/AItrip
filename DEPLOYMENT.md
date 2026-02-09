# AItrip 배포 가이드

AWS EC2 + RDS를 활용한 Docker 기반 배포 가이드입니다.

---

## 목차

1. [아키텍처 개요](#1-아키텍처-개요)
2. [사전 준비](#2-사전-준비)
3. [AWS RDS 설정](#3-aws-rds-설정)
4. [AWS EC2 설정](#4-aws-ec2-설정)
5. [배포 파일 구성](#5-배포-파일-구성)
6. [수동 배포](#6-수동-배포)
7. [GitHub Actions CI/CD](#7-github-actions-cicd)
8. [트러블슈팅](#8-트러블슈팅)

---

## 1. 아키텍처 개요

```
                    ┌─────────────────────────────────────────┐
                    │              AWS Cloud                   │
                    │                                          │
┌──────────┐        │  ┌──────────────────────────────────┐   │
│  Client  │───────────▶│           EC2 Instance            │   │
└──────────┘        │  │                                    │   │
                    │  │  ┌──────────┐  ┌─────────────────┐ │   │
                    │  │  │ Frontend │  │     Backend     │ │   │
                    │  │  │  :3000   │─▶│      :8080      │ │   │
                    │  │  └──────────┘  └────────┬────────┘ │   │
                    │  │                         │          │   │
                    │  │  ┌──────────┐  ┌────────▼────────┐ │   │
                    │  │  │    AI    │  │      Redis      │ │   │
                    │  │  │  :8000   │  │      :6379      │ │   │
                    │  │  └──────────┘  └─────────────────┘ │   │
                    │  └──────────────────────────────────┘   │
                    │              │                           │
                    │              ▼                           │
                    │  ┌──────────────────────────────────┐   │
                    │  │         RDS (MySQL 8.0)           │   │
                    │  │            :3306                  │   │
                    │  └──────────────────────────────────┘   │
                    └─────────────────────────────────────────┘
```

### 왜 이 구조인가?

| 구성 요소 | 로컬 (docker-compose.yml) | 운영 (EC2) | 이유 |
|-----------|---------------------------|------------|------|
| MySQL | 컨테이너 | RDS | 데이터 안정성, 자동 백업 |
| Redis | 컨테이너 | 컨테이너 | 세션 캐시용, 손실 허용 |
| Backend | 컨테이너 | 컨테이너 | 동일 |
| AI | 컨테이너 | 컨테이너 | 동일 |
| Frontend | 컨테이너 | 컨테이너 | Next.js SSR |

---

## 2. 사전 준비

### AWS 계정 준비

- AWS 계정 생성
- IAM 사용자 생성 (EC2, RDS 권한)
- 키 페어 생성 (EC2 접속용)

### 필요한 정보 정리

배포 전 아래 값들을 미리 정해두세요:

```
DB_NAME=aitrip
DB_USERNAME=admin
DB_PASSWORD=(강력한 비밀번호)
JWT_SECRET=(256비트 이상 비밀키)
```

---

## 3. AWS RDS 설정

### Step 1: RDS 인스턴스 생성

1. AWS Console → RDS → 데이터베이스 생성
2. 설정:
   - 엔진: MySQL 8.0
   - 템플릿: 프리 티어 (개발용) / 프로덕션 (운영용)
   - DB 인스턴스 식별자: `aitrip-db`
   - 마스터 사용자 이름: `admin`
   - 마스터 암호: (강력한 비밀번호 설정)

### Step 2: 연결 설정

1. VPC: EC2와 동일한 VPC 선택
2. 퍼블릭 액세스: 아니요 (보안)
3. VPC 보안 그룹: 새로 생성 또는 기존 그룹 선택

### Step 3: 보안 그룹 설정

EC2에서 RDS로 접근 허용:

| 유형 | 포트 | 소스 |
|------|------|------|
| MySQL/Aurora | 3306 | EC2 보안 그룹 ID |

### Step 4: 엔드포인트 확인

생성 완료 후 엔드포인트 복사:
```
aitrip-db.xxxxxxxxxxxx.ap-northeast-2.rds.amazonaws.com
```

---

## 4. AWS EC2 설정

### Step 1: EC2 인스턴스 생성

1. AWS Console → EC2 → 인스턴스 시작
2. 설정:
   - AMI: Amazon Linux 2023 또는 Ubuntu 22.04
   - 인스턴스 유형: t3.small (최소 권장)
   - 키 페어: 기존 선택 또는 새로 생성
   - 스토리지: 20GB 이상

### Step 2: 보안 그룹 설정

| 유형 | 포트 | 소스 | 용도 |
|------|------|------|------|
| SSH | 22 | 내 IP | 서버 접속 |
| HTTP | 80 | 0.0.0.0/0 | 웹 서비스 |
| HTTPS | 443 | 0.0.0.0/0 | 웹 서비스 (SSL) |
| 사용자 지정 TCP | 3000 | 0.0.0.0/0 | Frontend (Next.js) |
| 사용자 지정 TCP | 8080 | 0.0.0.0/0 | Backend API |
| 사용자 지정 TCP | 8000 | 0.0.0.0/0 | AI API |

### Step 3: Elastic IP 연결

1. EC2 → 탄력적 IP → 탄력적 IP 주소 할당
2. 할당된 IP → 작업 → 탄력적 IP 주소 연결
3. 생성한 EC2 인스턴스 선택

### Step 4: EC2 접속 및 Docker 설치

```bash
# EC2 접속
ssh -i your-key.pem ec2-user@<ELASTIC-IP>

# Docker 설치 (Amazon Linux 2023)
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 재접속 (docker 그룹 적용)
exit
ssh -i your-key.pem ec2-user@<ELASTIC-IP>

# 설치 확인
docker --version
docker-compose --version
```

Ubuntu의 경우:
```bash
# Docker 설치 (Ubuntu)
sudo apt update
sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu
```

---

## 5. 배포 파일 구성

### 5.1 docker-compose.prod.yml

EC2에서 사용할 운영용 Docker Compose 파일:

```yaml
# docker-compose.prod.yml
services:
  redis:
    image: redis:7-alpine
    container_name: aitrip-redis
    restart: always
    networks:
      - aitrip-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: aitrip-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=true&requireSSL=true&serverTimezone=UTC
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      redis:
        condition: service_healthy
    networks:
      - aitrip-network

  ai:
    build:
      context: ./ai
      dockerfile: Dockerfile
    container_name: aitrip-ai
    restart: always
    ports:
      - "8000:8000"
    environment:
      BACKEND_API_URL: http://backend:8080
      OPENAI_API_KEY: ${OPENAI_API_KEY}
    depends_on:
      - backend
    networks:
      - aitrip-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: aitrip-frontend
    restart: always
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://backend:8080
    depends_on:
      - backend
    networks:
      - aitrip-network

networks:
  aitrip-network:
    driver: bridge
```

### 5.2 .env 파일 (EC2에 직접 생성)

```bash
# EC2에서 .env 파일 생성
cat << 'EOF' > .env
# Database (RDS)
DB_HOST=aitrip-db.xxxxxxxxxxxx.ap-northeast-2.rds.amazonaws.com
DB_NAME=aitrip
DB_USERNAME=admin
DB_PASSWORD=your-strong-password

# JWT
JWT_SECRET=your-256-bit-secret-key-here-must-be-at-least-32-characters-long

# AI (필요한 경우)
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxx
EOF
```

> **보안 주의**: `.env` 파일은 절대 Git에 커밋하지 마세요!

### 5.3 .gitignore 확인

```gitignore
# 환경 변수 파일
.env
.env.local
.env.prod
```

---

## 6. 수동 배포

### Step 1: 프로젝트 클론

```bash
# EC2에서 실행
cd ~
git clone https://github.com/your-org/aitrip.git
cd aitrip
```

### Step 2: 환경 변수 설정

```bash
# .env 파일 생성 (위의 5.2 참조)
nano .env
```

### Step 3: 빌드 및 실행

```bash
# 운영 환경으로 실행
docker-compose -f docker-compose.prod.yml up -d --build

# 로그 확인
docker-compose -f docker-compose.prod.yml logs -f

# 상태 확인
docker-compose -f docker-compose.prod.yml ps
```

### Step 4: 동작 확인

```bash
# Frontend 접속 확인
curl http://localhost:3000

# Backend 헬스체크
curl http://localhost:8080/actuator/health

# AI 헬스체크
curl http://localhost:8000/health
```

---

## 7. GitHub Actions CI/CD

### 7.1 GitHub Secrets 설정

Repository → Settings → Secrets and variables → Actions:

| Secret 이름 | 값 |
|------------|-----|
| `EC2_HOST` | EC2 Elastic IP |
| `EC2_USERNAME` | ec2-user 또는 ubuntu |
| `EC2_SSH_KEY` | 프라이빗 키 전체 내용 |
| `DB_HOST` | RDS 엔드포인트 |
| `DB_NAME` | aitrip |
| `DB_USERNAME` | admin |
| `DB_PASSWORD` | RDS 비밀번호 |
| `JWT_SECRET` | JWT 시크릿 키 |
| `OPENAI_API_KEY` | OpenAI API 키 (선택) |

### 7.2 Workflow 파일

```yaml
# .github/workflows/deploy.yml
name: Deploy to EC2

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Deploy to EC2
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ${{ secrets.EC2_USERNAME }}
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            cd ~/aitrip

            # 최신 코드 가져오기
            git pull origin main

            # .env 파일 생성
            cat << EOF > .env
            DB_HOST=${{ secrets.DB_HOST }}
            DB_NAME=${{ secrets.DB_NAME }}
            DB_USERNAME=${{ secrets.DB_USERNAME }}
            DB_PASSWORD=${{ secrets.DB_PASSWORD }}
            JWT_SECRET=${{ secrets.JWT_SECRET }}
            OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}
            EOF

            # 재빌드 및 재시작
            docker-compose -f docker-compose.prod.yml down
            docker-compose -f docker-compose.prod.yml up -d --build

            # 이전 이미지 정리
            docker image prune -f
```

### 7.3 사용 방법

1. `main` 브랜치에 push하면 자동 배포
2. 수동 배포: Actions → Deploy to EC2 → Run workflow

---

## 8. 트러블슈팅

### Backend가 시작되지 않음

```bash
# 로그 확인
docker-compose -f docker-compose.prod.yml logs backend

# 일반적인 원인:
# 1. RDS 연결 실패 → 보안 그룹 확인
# 2. 환경 변수 누락 → .env 파일 확인
# 3. Redis 연결 실패 → Redis 컨테이너 상태 확인
```

### RDS 연결 실패

```bash
# EC2에서 RDS 연결 테스트
sudo yum install -y mysql
mysql -h <RDS-ENDPOINT> -u admin -p

# 연결 안 되면:
# 1. RDS 보안 그룹에서 EC2 보안 그룹 허용 확인
# 2. RDS가 같은 VPC에 있는지 확인
```

### 디스크 공간 부족

```bash
# Docker 정리
docker system prune -a

# 사용량 확인
df -h
```

### 컨테이너 재시작 반복

```bash
# 상태 확인
docker-compose -f docker-compose.prod.yml ps

# 특정 컨테이너 로그
docker logs aitrip-backend --tail 100
```

---

## 체크리스트

배포 전 확인사항:

- [ ] RDS 인스턴스 생성 완료
- [ ] RDS 보안 그룹에 EC2 허용 추가
- [ ] EC2 인스턴스 생성 완료
- [ ] EC2에 Docker, Docker Compose 설치
- [ ] Elastic IP 연결
- [ ] EC2에 프로젝트 클론
- [ ] .env 파일 생성 (민감 정보 입력)
- [ ] docker-compose.prod.yml 실행
- [ ] 헬스체크 통과 확인
- [ ] GitHub Secrets 설정 (CI/CD 사용 시)

---

## 추가 참고

- [Backend 개발 가이드](./backend/README.md)
- [로컬 개발 환경 가이드](./DEVELOPMENT.md)

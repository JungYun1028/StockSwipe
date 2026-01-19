# StockSwipe - 설치 및 실행 가이드

TypeScript에서 JavaScript + Java Spring Boot로 변환된 StockSwipe 프로젝트입니다.

## 🏗️ 프로젝트 구조

```
dist/
├── backend/                 # Java Spring Boot API 서버
│   ├── src/
│   │   └── main/
│   │       ├── java/com/stockswipe/
│   │       │   ├── controller/      # REST API 컨트롤러
│   │       │   ├── service/         # 비즈니스 로직
│   │       │   ├── model/           # JPA 엔티티
│   │       │   ├── dto/             # 데이터 전송 객체
│   │       │   └── repository/      # JPA 레포지토리
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
└── src/                     # React 프론트엔드 (JavaScript)
    ├── components/          # 재사용 컴포넌트
    ├── pages/               # 페이지 컴포넌트
    ├── context/             # React Context
    ├── services/            # API 서비스
    └── data/                # Mock 데이터
```

## 📋 필요 조건

### 백엔드
- **Java 21** (중요: 21 필수!)
- Maven 3.6 이상
- PostgreSQL 15 이상

### 프론트엔드
- Node.js 18 이상
- npm 또는 yarn

### API 키
- `secret.json` 파일 (프로젝트 루트에 생성)
- 임시 실행 시 빈 값으로 생성 가능

**빠른 시작은 [QUICKSTART.md](./QUICKSTART.md)를 참고하세요!**

## 🚀 실행 방법

### 빠른 시작 (자동 스크립트)

macOS/Linux:
```bash
# 백엔드 실행 (PostgreSQL, 데이터베이스 자동 설정)
./start-backend.sh

# 새 터미널에서 프론트엔드 실행
npm install
npm run dev
```

Windows 또는 수동 실행은 아래 참조.

### 1. 백엔드 실행 (포트 8080)

**전제 조건:**
- PostgreSQL이 실행 중이어야 함
- `stockswipe` 데이터베이스가 생성되어 있어야 함
- `secret.json` 파일이 프로젝트 루트에 있어야 함

```bash
cd backend
mvn spring-boot:run
```

백엔드가 성공적으로 실행되면:
- API 서버: http://localhost:8080
- 종목 API: http://localhost:8080/api/stocks
- 총 160개 종목 데이터 자동 생성 (카테고리별 20개씩)

### 2. 프론트엔드 실행 (포트 5173)

새 터미널을 열고:

```bash
# 프로젝트 루트에서
npm install
npm run dev
```

프론트엔드가 성공적으로 실행되면:
- 웹앱: http://localhost:5173

## 🔧 환경 설정

### API 키 설정 (필수!)

프로젝트 루트에 `secret.json` 파일 생성:

```json
{
  "stock_api": "YOUR_STOCK_API_KEY_HERE",
  "openai_api_key": "YOUR_OPENAI_API_KEY_HERE"
}
```

**임시 테스트용 (빈 값):**
```bash
# macOS/Linux
cat > secret.json << 'EOF'
{
  "stock_api": "",
  "openai_api_key": ""
}
EOF
```

### 백엔드 설정

`backend/src/main/resources/application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/stockswipe
spring.datasource.username=postgres
spring.datasource.password=postgres

# OpenAI API 키 (secret.json에서 자동 로드)
openai.api.key=${OPENAI_API_KEY:your-openai-api-key-here}
```

### 프론트엔드 설정

프론트엔드는 `vite.config.js`에서 백엔드 프록시가 자동 설정되어 있습니다.
추가 설정 불필요!

## 📡 API 엔드포인트

### 종목 관련
- `GET /api/stocks` - 전체 종목 조회
- `GET /api/stocks/{stockId}` - 특정 종목 조회
- `POST /api/stocks/by-categories` - 카테고리별 종목 조회

### 카테고리 관련
- `GET /api/categories` - 전체 카테고리 조회

## 🗄️ 데이터베이스

### PostgreSQL 설치 및 설정

#### macOS (Homebrew)
```bash
brew install postgresql@15
brew services start postgresql@15

# 데이터베이스 생성
createdb stockswipe
```

#### Ubuntu/Linux
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql

# 데이터베이스 생성
sudo -u postgres psql
CREATE DATABASE stockswipe;
\q
```

#### Windows
PostgreSQL 공식 사이트에서 설치 프로그램 다운로드:
https://www.postgresql.org/download/windows/

**데이터베이스 정보:**
- 데이터베이스명: `stockswipe`
- 포트: `5432`
- 사용자: `postgres`
- 비밀번호: `postgres` (application.properties에서 설정)

**총 80개 종목 자동 생성!** (카테고리별 10개씩)

## 📦 빌드

### 백엔드 빌드

```bash
cd backend
mvn clean package
```

빌드된 JAR: `backend/target/stockswipe-api-1.0.0.jar`

### 프론트엔드 빌드

```bash
npm run build
```

빌드된 파일: `dist/` 디렉토리

## 🐛 문제 해결

### CORS 오류
백엔드에서 CORS가 이미 설정되어 있습니다. 문제가 발생하면:
1. 백엔드가 8080 포트에서 실행 중인지 확인
2. 프론트엔드가 5173 포트에서 실행 중인지 확인

### API 연결 오류
1. 백엔드가 먼저 실행되었는지 확인
2. `.env` 파일에 올바른 API URL이 설정되었는지 확인
3. 브라우저 콘솔에서 네트워크 오류 확인

### 포트 충돌
다른 애플리케이션이 8080 또는 5173 포트를 사용 중이면:
- 백엔드: `application.properties`에서 `server.port` 변경
- 프론트엔드: `vite.config.js`에서 `server.port` 변경

## 🎯 주요 변경사항

### TypeScript → JavaScript 변환
- 모든 `.tsx` → `.jsx` 변환
- 모든 `.ts` → `.js` 변환
- 타입 정의 제거

### Mock 데이터 → Java API
- 프론트엔드의 `mockStocks.ts`는 참조용으로만 유지
- 실제 데이터는 Java Spring Boot API에서 제공
- Axios를 통한 API 통신 추가

### 새로 추가된 기능
- `src/services/api.js` - Axios 기반 API 클라이언트
- Java Spring Boot 백엔드 전체
- H2 데이터베이스 통합
- CORS 설정

## 📝 다음 단계 (AWS 배포)

### 프론트엔드
1. S3 버킷 생성
2. `npm run build` 실행
3. dist/ 폴더를 S3에 업로드
4. CloudFront 배포 생성

### 백엔드
1. EC2 인스턴스 생성 (Amazon Linux 2)
2. Java 17 설치
3. JAR 파일 업로드
4. `java -jar stockswipe-api-1.0.0.jar` 실행
5. RDS (MySQL/PostgreSQL)로 H2 대체

### 데이터베이스
1. RDS 인스턴스 생성
2. `application.properties` 수정
3. 실제 주식 API 연동 (한국투자증권, 키움 등)

## 📞 문의

문제가 발생하면 이슈를 생성해주세요.


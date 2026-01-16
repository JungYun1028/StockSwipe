# StockSwipe 프로젝트 구조

## 📁 디렉토리 구조

```
dist/
├── backend/                          # Java Spring Boot 백엔드
│   ├── src/
│   │   └── main/
│   │       ├── java/com/stockswipe/
│   │       │   ├── StockSwipeApplication.java
│   │       │   ├── controller/
│   │       │   │   └── StockController.java
│   │       │   ├── service/
│   │       │   │   ├── StockService.java
│   │       │   │   └── StockDataInitializer.java    # ★ 80개 종목 데이터
│   │       │   ├── model/
│   │       │   │   ├── Stock.java
│   │       │   │   ├── ChartData.java
│   │       │   │   ├── News.java
│   │       │   │   ├── MovingAverage.java
│   │       │   │   ├── AnalystRating.java
│   │       │   │   └── Category.java
│   │       │   ├── dto/
│   │       │   │   ├── StockDTO.java
│   │       │   │   ├── ChartDataDTO.java
│   │       │   │   ├── NewsDTO.java
│   │       │   │   ├── MovingAverageDTO.java
│   │       │   │   └── AnalystRatingDTO.java
│   │       │   └── repository/
│   │       │       └── StockRepository.java
│   │       └── resources/
│   │           └── application.properties           # PostgreSQL 설정
│   ├── pom.xml                                     # Maven 의존성
│   └── README.md
│
├── src/                              # React 프론트엔드 (JavaScript)
│   ├── components/                   # 재사용 컴포넌트 (*.jsx)
│   │   ├── FloatingChatbot.jsx
│   │   ├── FloatingChatbot.module.css
│   │   ├── MiniChart.jsx
│   │   ├── Navigation.jsx
│   │   ├── Navigation.module.css
│   │   ├── PreviewCards.jsx
│   │   ├── PreviewCards.module.css
│   │   ├── StockCard.jsx
│   │   ├── StockCard.module.css
│   │   ├── SwipeContainer.jsx
│   │   └── SwipeContainer.module.css
│   ├── pages/                        # 페이지 컴포넌트 (*.jsx)
│   │   ├── CategorySelect.jsx
│   │   ├── CategorySelect.module.css
│   │   ├── Home.jsx
│   │   ├── Home.module.css
│   │   ├── NewsDetail.jsx
│   │   ├── NewsDetail.module.css
│   │   ├── StockDetail.jsx
│   │   ├── StockDetail.module.css
│   │   ├── WatchList.jsx
│   │   └── WatchList.module.css
│   ├── context/
│   │   └── AppContext.jsx           # React Context (전역 상태)
│   ├── services/
│   │   └── api.js                   # ★ Axios API 클라이언트
│   ├── data/
│   │   └── mockStocks.js            # 참고용 (실제론 DB 사용)
│   ├── assets/
│   ├── App.jsx
│   ├── App.css
│   ├── main.jsx
│   └── index.css
│
├── public/
│   └── vite.svg
│
├── package.json                      # 프론트엔드 의존성
├── vite.config.js                    # Vite 설정
├── eslint.config.js
├── .gitignore
├── README.md                         # 프로젝트 소개
├── SETUP.md                          # 설치 및 실행 가이드
├── PROJECT_SUMMARY.md                # 변환 완료 보고서
└── PROJECT_STRUCTURE.md              # 이 문서
```

---

## 🗄️ 데이터베이스 (PostgreSQL)

### 데이터베이스 설정
- **데이터베이스명**: `stockswipe`
- **포트**: `5432`
- **사용자**: `postgres`
- **비밀번호**: `postgres` (개발용, 프로덕션에서 변경 필요)

### 테이블 구조

#### 1. stocks
```sql
CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    stock_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    ticker VARCHAR(255) NOT NULL,
    current_price DOUBLE PRECISION,
    previous_close DOUBLE PRECISION,
    high DOUBLE PRECISION,
    low DOUBLE PRECISION,
    volume BIGINT,
    buy_volume BIGINT,
    sell_volume BIGINT,
    description TEXT,
    business TEXT,
    rsi INTEGER,
    ma20 DOUBLE PRECISION,
    ma60 DOUBLE PRECISION,
    ma120 DOUBLE PRECISION,
    rating VARCHAR(50),
    reason TEXT
);
```

#### 2. stock_categories
```sql
CREATE TABLE stock_categories (
    stock_id BIGINT REFERENCES stocks(id),
    category VARCHAR(255)
);
```

#### 3. stock_keywords
```sql
CREATE TABLE stock_keywords (
    stock_id BIGINT REFERENCES stocks(id),
    keyword VARCHAR(255)
);
```

#### 4. chart_data
```sql
CREATE TABLE chart_data (
    id BIGSERIAL PRIMARY KEY,
    time VARCHAR(255),
    price DOUBLE PRECISION,
    stock_id BIGINT REFERENCES stocks(id)
);
```

#### 5. news
```sql
CREATE TABLE news (
    id BIGSERIAL PRIMARY KEY,
    news_id VARCHAR(255),
    title TEXT,
    summary TEXT,
    stock_id BIGINT REFERENCES stocks(id)
);
```

### 초기 데이터
**총 80개 종목** (카테고리별 10개씩)

| 카테고리 | 종목 수 | 예시 |
|---------|---------|------|
| 바이오 | 10개 | 삼성바이오로직스/207940, 셀트리온/068270 |
| AI | 10개 | 네이버/035420, 카카오/035720 |
| 선박 | 10개 | HD한국조선해양/009540, 삼성중공업/010140 |
| 식품 | 10개 | CJ제일제당/097950, 오리온/271560 |
| 에너지 | 10개 | 한화솔루션/009830, 한국전력/015760 |
| 반도체 | 10개 | SK하이닉스/000660, 삼성전자/005930 |
| 금융 | 10개 | KB금융/105560, 신한지주/055550 |
| 2차전지 | 10개 | LG에너지솔루션/373220, 삼성SDI/006400 |

---

## 🔌 API 엔드포인트

### 종목 관련
- `GET /api/stocks` - 전체 80개 종목 조회
- `GET /api/stocks/{stockId}` - 특정 종목 상세 조회
- `POST /api/stocks/by-categories` - 카테고리별 종목 필터링

### 카테고리 관련
- `GET /api/categories` - 8개 카테고리 조회

---

## 🏗️ 주요 파일 설명

### Backend

#### `StockDataInitializer.java`
- **역할**: 애플리케이션 시작 시 80개 종목을 PostgreSQL DB에 자동 저장
- **메서드**:
  - `createBioStocks()` - 바이오 10개
  - `createAIStocks()` - AI 10개
  - `createShipStocks()` - 선박 10개
  - `createFoodStocks()` - 식품 10개
  - `createEnergyStocks()` - 에너지 10개
  - `createSemiStocks()` - 반도체 10개
  - `createFinanceStocks()` - 금융 10개
  - `createBatteryStocks()` - 2차전지 10개

#### `StockController.java`
- **역할**: REST API 엔드포인트 제공
- **주요 메서드**:
  - `getAllStocks()` - 전체 종목 조회
  - `getStockById(String stockId)` - 종목 상세 조회
  - `getCategories()` - 카테고리 목록
  - `getStocksByCategories(List<String> categories)` - 카테고리별 필터링

#### `StockService.java`
- **역할**: 비즈니스 로직 처리
- **주요 메서드**:
  - `getAllStocks()` - Repository에서 데이터 조회 후 DTO 변환
  - `getStockById(String stockId)` - 특정 종목 조회
  - `getStocksByCategories(List<String> categories)` - 카테고리 필터링

### Frontend

#### `src/services/api.js`
- **역할**: Axios 기반 API 호출
- **주요 함수**:
  - `getAllStocks()` - 백엔드 `/api/stocks` 호출
  - `getStockById(stockId)` - 백엔드 `/api/stocks/{id}` 호출
  - `getCategories()` - 백엔드 `/api/categories` 호출

#### `src/context/AppContext.jsx`
- **역할**: 전역 상태 관리 (React Context API)
- **상태**:
  - `allStocks` - API에서 가져온 전체 종목
  - `selectedCategories` - 선택된 카테고리
  - `likedStocks` - 좋아요한 종목
  - `swipeHistory` - 스와이프 히스토리

---

## 🚀 실행 순서

### 1. PostgreSQL 설치 및 DB 생성
```bash
# macOS (Homebrew)
brew install postgresql@15
brew services start postgresql@15

# 데이터베이스 생성
createdb stockswipe

# 또는 psql 접속 후
psql postgres
CREATE DATABASE stockswipe;
\q
```

### 2. 백엔드 실행
```bash
cd backend
mvn spring-boot:run
```
→ 서버 시작 시 자동으로 80개 종목 데이터 생성 ✅

### 3. 프론트엔드 실행
```bash
# 프로젝트 루트에서
npm install
npm run dev
```

---

## 📦 의존성

### Backend (pom.xml)
- Spring Boot 3.2.1
- Spring Data JPA
- PostgreSQL Driver
- Lombok

### Frontend (package.json)
- React 19.2.0
- Axios
- React Router DOM
- Framer Motion
- Recharts
- Lucide React

---

## 🔧 설정 파일

### `backend/src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stockswipe
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=create-drop
```

### `vite.config.js`
```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

---

## 📝 변경 이력

### v2.0.0 (2026-01-14)
- ✅ TypeScript → JavaScript 완전 변환
- ✅ Mock 데이터 → PostgreSQL DB 마이그레이션
- ✅ 10개 → 80개 종목으로 확대 (카테고리별 10개)
- ✅ H2 → PostgreSQL 변경
- ✅ 프로젝트 구조 정리 (Backend/Frontend 분리)

### v1.0.0 (초기 버전)
- TypeScript React + Vercel
- Mock 데이터 10개 종목
- H2 인메모리 DB

---

## 🐛 문제 해결

### PostgreSQL 연결 오류
```bash
# PostgreSQL이 실행 중인지 확인
brew services list

# PostgreSQL 시작
brew services start postgresql@15

# 데이터베이스 존재 확인
psql postgres -c "\l"
```

### 데이터 초기화
```bash
# 백엔드 재시작 시 자동으로 데이터 재생성
# application.properties에서 ddl-auto=create-drop 설정됨
```

---

**작성일**: 2026-01-14  
**버전**: 2.0.0


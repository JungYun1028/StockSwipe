# StockSwipe Backend API

Java Spring Boot 기반 REST API 서버입니다.

## 🚀 실행 방법

### 필요 조건
- Java 17 이상
- Maven 3.6 이상

### 개발 서버 실행

```bash
cd backend
mvn spring-boot:run
```

서버는 `http://localhost:8080`에서 실행됩니다.

## 📡 API 엔드포인트

### 종목 관련
- `GET /api/stocks` - 전체 종목 조회
- `GET /api/stocks/{stockId}` - 특정 종목 조회
- `POST /api/stocks/by-categories` - 카테고리별 종목 조회

### 카테고리 관련
- `GET /api/categories` - 전체 카테고리 조회

## 🗄️ 데이터베이스

개발 환경에서는 H2 인메모리 데이터베이스를 사용합니다.
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:stockswipe`
- Username: `sa`
- Password: (비어있음)

## 📦 빌드

```bash
mvn clean package
```

빌드된 JAR 파일은 `target/` 디렉토리에 생성됩니다.

## 🔧 설정

`src/main/resources/application.properties`에서 설정을 변경할 수 있습니다.


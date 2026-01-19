# 팀원을 위한 빠른 설정 가이드 🚀

## 📋 개요

StockSwipe 프로젝트를 처음 실행하는 팀원을 위한 가이드입니다.
두 가지 방법 중 선택하여 데이터를 설정할 수 있습니다.

---

## 🔧 사전 준비

다음이 설치되어 있어야 합니다:
- ✅ Java 21
- ✅ Maven 3.9+
- ✅ PostgreSQL 15+
- ✅ Node.js 18+

자세한 설치 방법: [QUICKSTART.md](./QUICKSTART.md) 참고

---

## 🗄️ 데이터베이스 설정

### 1. PostgreSQL 데이터베이스 생성

```bash
# macOS/Linux
createdb stockswipe

# Windows (psql)
psql -U postgres
CREATE DATABASE stockswipe;
\q
```

### 2. 초기 데이터 생성 (2가지 방법)

#### 방법 1: 백엔드 자동 실행 (추천) ⭐

백엔드를 실행하면 **자동으로** 데이터가 생성됩니다!

```bash
# 백엔드 실행
./start-backend.sh
# 또는
cd backend && mvn spring-boot:run

# ✅ 자동으로 실행됨:
#    - 카테고리 8개 생성
#    - 종목 160개 생성 (stock_id, name만)
```

**장점:**
- 간단함 (백엔드 실행만 하면 됨)
- 중복 방지 자동 처리
- 코드 업데이트 시 자동 반영

#### 방법 2: SQL 파일 직접 실행

```bash
# 프로젝트 루트에서
psql -d stockswipe -f backend/init-data.sql

# ✅ 실행되는 내용:
#    - 카테고리 8개 INSERT
#    - 종목 160개 INSERT
#    - 데이터 확인 쿼리 자동 실행
```

**장점:**
- 빠름 (SQL 직접 실행)
- 백엔드 없이 데이터 생성 가능

---

## 📊 주식 데이터 업데이트 (선택사항)

기본 데이터(stock_id, name)는 위에서 생성되었습니다.
실제 **주가 정보**(종가, 등락률, 시가총액)를 가져오려면:

### 1. API 키 설정

프로젝트 루트에 `secret.json` 파일 생성:

```json
{
  "stock_api": "YOUR_STOCK_API_KEY_HERE",
  "openai_api_key": ""
}
```

> ⚠️ API 키가 없으면 이 단계는 건너뛰세요. 기본 데이터만으로도 프론트엔드 테스트 가능합니다.

### 2. 주식 데이터 업데이트 스크립트 실행

```bash
./fetch-stock-data.sh
```

**실행 내용:**
- secret.json에서 API 키 자동 로드
- 백엔드가 없으면 자동 시작
- 160개 종목 실시간 주가 데이터 업데이트 (약 3분 소요)
- 진행 상황 실시간 표시

---

## 🎯 요약 (TL;DR)

```bash
# 1. 코드 받기
git pull origin develop

# 2. PostgreSQL DB 생성
createdb stockswipe

# 3. 백엔드 실행 (자동으로 데이터 생성됨)
./start-backend.sh

# 4. (선택) 실제 주가 데이터 가져오기
./fetch-stock-data.sh  # secret.json에 API 키 필요

# 5. 프론트엔드 실행
npm install
npm run dev

# 6. 브라우저에서 확인
# http://localhost:5173
```

---

## 📂 파일 위치 정리

### 초기 데이터 생성
| 파일 | 용도 | 실행 방법 |
|------|------|-----------|
| `StockDataInitializer.java` | 백엔드 시작 시 자동 실행 | 백엔드 실행만 하면 됨 |
| `backend/init-data.sql` | SQL 직접 실행 | `psql -d stockswipe -f backend/init-data.sql` |

### 주식 데이터 업데이트
| 파일 | 용도 | 실행 방법 |
|------|------|-----------|
| `fetch-stock-data.sh` | 주가 정보 업데이트 | `./fetch-stock-data.sh` |

---

## 🐛 문제 해결

### "데이터가 이미 존재합니다" 메시지
✅ 정상입니다! 중복 방지 기능이 작동 중입니다.

### "Database does not exist"
```bash
createdb stockswipe
```

### "Permission denied: ./fetch-stock-data.sh"
```bash
chmod +x fetch-stock-data.sh
chmod +x start-backend.sh
```

### 백엔드 시작 실패
1. Java 21 설치 확인: `java -version`
2. Maven 설치 확인: `mvn -version`
3. PostgreSQL 실행 확인: `psql -d stockswipe -c "SELECT 1;"`

---

## 📞 도움이 필요하면

- **QUICKSTART.md**: 상세 설치 가이드
- **SETUP.md**: 전체 프로젝트 설정
- **이슈 생성** 또는 팀원에게 문의

---

## ✅ 확인 사항

다음 명령어로 데이터가 제대로 생성되었는지 확인:

```bash
# PostgreSQL 접속
psql -d stockswipe

# 데이터 확인
SELECT COUNT(*) FROM categories;  -- 8개여야 함
SELECT COUNT(*) FROM stocks;      -- 160개여야 함

# 샘플 데이터 확인
SELECT s.stock_id, s.name, c.name AS category 
FROM stocks s 
JOIN categories c ON s.category_id = c.id 
LIMIT 5;

\q
```

**기대 결과:**
- Categories: 8개
- Stocks: 160개
- 각 종목에 stock_id, name, category_id가 있어야 함

---

**처음 설정은 약 10-15분 소요됩니다.**
**주가 데이터 업데이트는 추가로 약 3분 소요됩니다.**

🎉 **설정 완료 후 http://localhost:5173 에서 앱을 확인하세요!**

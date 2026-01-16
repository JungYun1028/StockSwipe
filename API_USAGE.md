# 📊 주식 데이터 업데이트 가이드

## 🚀 빠른 시작 (추천)

### 1️⃣ API 키로 실행
```bash
cd /Users/jejeong-yun/stockswipe
./update-stocks.sh YOUR_API_KEY
```

### 2️⃣ secret.json 사용 (자동)
```bash
cd /Users/jejeong-yun/stockswipe
./update-stocks.sh
```
※ secret.json에 `stock_api` 키가 있으면 자동으로 읽어옵니다.

---

## 📝 수동 실행 (상세)

### Step 1: 백엔드 실행
```bash
export STOCK_API_KEY='여기에-API-키-입력'
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
cd /Users/jejeong-yun/stockswipe/backend
mvn spring-boot:run
```

### Step 2: API 호출
**새 터미널에서 실행:**
```bash
curl -X POST http://localhost:8080/api/stocks/update-from-api
```

### Step 3: 로그 확인
```bash
tail -f /tmp/spring-boot.log | grep "업데이트"
```

### Step 4: 결과 확인
```bash
curl http://localhost:8080/api/stocks | python3 -m json.tool | head -50
```

---

## 🔍 데이터 확인 명령어

### 전체 종목 수 확인
```bash
curl -s http://localhost:8080/api/stocks | python3 -c "import sys, json; print(f'{len(json.load(sys.stdin))}개 종목')"
```

### 특정 종목 확인
```bash
# 삼성전자 (005930)
curl -s http://localhost:8080/api/stocks/005930 | python3 -m json.tool
```

### 카테고리별 종목 확인
```bash
# 반도체 카테고리
curl -s http://localhost:8080/api/stocks/category/semi | python3 -m json.tool
```

### 업데이트된 데이터 샘플
```bash
curl -s http://localhost:8080/api/stocks | python3 -c "
import sys, json
data = json.load(sys.stdin)
for stock in data[:3]:  # 처음 3개만
    print(f'{stock[\"name\"]}: {stock.get(\"clpr\", \"데이터없음\")}원')
"
```

---

## ⚙️ API 키 설정 방법

### 방법 1: 환경변수
```bash
export STOCK_API_KEY='your-api-key-here'
```

### 방법 2: secret.json
```json
{
  "stock_api": "your-api-key-here",
  "openai_api_key": "your-openai-key-here"
}
```

### 방법 3: application.properties
```properties
stock.api.key=your-api-key-here
```

---

## 🐛 문제 해결

### 401 Unauthorized 에러
→ API 키가 잘못되었거나 만료됨. 키를 확인하세요.

### 백엔드가 시작되지 않음
```bash
# Java 버전 확인
java -version  # Java 21 이상 필요

# 포트 확인
lsof -ti:8080  # 다른 프로세스가 사용 중인지 확인
```

### 데이터가 업데이트되지 않음
```bash
# 로그 확인
tail -100 /tmp/spring-boot.log

# 백엔드 재시작
pkill -f spring-boot:run
cd backend && mvn spring-boot:run
```

---

## 📊 업데이트되는 데이터

- ✅ 기준일자 (basDt)
- ✅ 시장구분 (mrktCtg) - KOSPI/KOSDAQ/KONEX
- ✅ 종가 (clpr)
- ✅ 전일대비 (vs)
- ✅ 등락률 (fltRt)
- ✅ 시가/고가/저가 (mkp/hipr/lopr)
- ✅ 거래량 (trqu)
- ✅ 거래대금 (trPrc)
- ✅ 상장주식수 (lstgStCnt)
- ✅ 시가총액 (mrktTotAmt)

---

## 🕐 예상 소요 시간

- **160개 종목**: 약 **16초** (초당 10건 제한)
- 백엔드 시작: 약 **30초**
- **총 소요 시간**: 약 **1분**

---

## 💡 자동화 (선택사항)

### 매일 자동 업데이트 (cron)
```bash
# crontab 편집
crontab -e

# 매일 오전 9시에 실행
0 9 * * * cd /Users/jejeong-yun/stockswipe && ./update-stocks.sh >> /tmp/stock-update.log 2>&1
```

### 백엔드 시작 스크립트
```bash
cat > /Users/jejeong-yun/stockswipe/start-backend.sh << 'EOF'
#!/bin/bash
export STOCK_API_KEY=$(cat secret.json | python3 -c "import sys, json; print(json.load(sys.stdin)['stock_api'])")
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
cd backend
mvn spring-boot:run
EOF

chmod +x start-backend.sh
```


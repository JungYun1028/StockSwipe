#!/bin/bash

echo "🚀 StockSwipe 백엔드 시작"
echo "=========================================="
echo ""

# 1. PostgreSQL 확인
echo "1️⃣  PostgreSQL 서비스 확인..."
if pgrep -x postgres > /dev/null; then
    echo "✅ PostgreSQL 실행 중"
else
    echo "⚠️  PostgreSQL이 실행되지 않았습니다"
    echo "   실행 명령: brew services start postgresql@15"
    exit 1
fi
echo ""

# 2. 데이터베이스 생성 (이미 있으면 스킵)
echo "2️⃣  데이터베이스 확인..."
PSQL="/opt/homebrew/Cellar/postgresql@15/15.15_1/bin/psql"
$PSQL postgres -lqt | cut -d \| -f 1 | grep -qw stockswipe
if [ $? -eq 0 ]; then
    echo "✅ stockswipe 데이터베이스 존재"
else
    echo "📝 stockswipe 데이터베이스 생성 중..."
    $PSQL postgres -c "CREATE DATABASE stockswipe;"
    echo "✅ 데이터베이스 생성 완료"
fi
echo ""

# 3. API 키 설정
echo "3️⃣  API 키 설정..."
cd /Users/jejeong-yun/stockswipe

if [ -f "secret.json" ]; then
    export STOCK_API_KEY=$(grep -o '"stock_api" : "[^"]*"' secret.json | cut -d '"' -f 4)
    export OPENAI_API_KEY=$(grep -o '"openai_api_key" : "[^"]*"' secret.json | cut -d '"' -f 4)
    echo "✅ API 키 로드 완료"
    echo "   - Stock API: ${STOCK_API_KEY:0:20}..."
    echo "   - OpenAI API: ${OPENAI_API_KEY:0:20}..."
else
    echo "⚠️  secret.json 파일을 찾을 수 없습니다"
fi
echo ""

# 4. 백엔드 실행
echo "4️⃣  Spring Boot 백엔드 시작..."
echo "   (Maven 빌드 및 실행 - 약 1~2분 소요)"
echo ""

cd backend

# 로그 디렉토리 생성
mkdir -p logs

# 백그라운드 실행 및 로그 저장
nohup mvn spring-boot:run > logs/spring.log 2>&1 &
BACKEND_PID=$!

echo "✅ 백엔드 시작됨 (PID: $BACKEND_PID)"
echo ""

# 5. 시작 대기
echo "5️⃣  서버 시작 대기 중..."
for i in {1..60}; do
    if curl -s http://localhost:8080/api/stocks > /dev/null 2>&1; then
        echo ""
        echo "✅ 백엔드 서버 시작 완료!"
        echo ""
        echo "=========================================="
        echo "🎉 서버 정보"
        echo "=========================================="
        echo "📡 API: http://localhost:8080"
        echo "📊 종목 조회: http://localhost:8080/api/stocks"
        echo "📁 카테고리: http://localhost:8080/api/categories"
        echo ""
        echo "📋 로그 확인: tail -f logs/spring.log"
        echo "🛑 서버 중지: kill $BACKEND_PID"
        echo "=========================================="
        echo ""
        
        # 데이터 확인
        sleep 3
        cd ..
        ./check-db.sh
        exit 0
    fi
    echo -n "."
    sleep 1
done

echo ""
echo "⚠️  서버 시작 시간 초과 (60초)"
echo "   로그 확인: tail -f logs/spring.log"


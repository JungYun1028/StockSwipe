#!/bin/bash

# 🚀 주식 데이터 업데이트 스크립트
# 사용법: ./update-stocks.sh YOUR_API_KEY

echo "======================================"
echo "📊 StockSwipe 주식 데이터 업데이트"
echo "======================================"
echo ""

# API 키 확인
if [ -z "$1" ]; then
    echo "❌ API 키가 필요합니다!"
    echo ""
    echo "사용법:"
    echo "  ./update-stocks.sh YOUR_API_KEY"
    echo ""
    echo "또는 환경변수로 설정:"
    echo "  export STOCK_API_KEY='YOUR_API_KEY'"
    echo "  ./update-stocks.sh"
    echo ""
    
    # secret.json에서 시도
    if [ -f "secret.json" ]; then
        echo "💡 secret.json에서 API 키를 찾는 중..."
        if command -v python3 &> /dev/null; then
            API_KEY=$(python3 -c "import json; print(json.load(open('secret.json'))['stock_api'])" 2>/dev/null)
            if [ ! -z "$API_KEY" ]; then
                echo "✅ secret.json에서 API 키를 찾았습니다!"
            fi
        fi
    fi
    
    if [ -z "$API_KEY" ] && [ -z "$STOCK_API_KEY" ]; then
        exit 1
    fi
fi

# API 키 설정
if [ ! -z "$1" ]; then
    export STOCK_API_KEY="$1"
elif [ -z "$STOCK_API_KEY" ]; then
    export STOCK_API_KEY="$API_KEY"
fi

echo "🔑 API 키: ${STOCK_API_KEY:0:30}..."
echo ""

# 1. 백엔드가 실행 중인지 확인
if lsof -ti:8080 > /dev/null 2>&1; then
    echo "✅ 백엔드가 이미 실행 중입니다."
else
    echo "🚀 백엔드를 시작합니다..."
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
    cd backend
    nohup mvn spring-boot:run > /tmp/spring-boot.log 2>&1 &
    echo "⏳ 백엔드 시작 대기 중... (30초)"
    sleep 30
    cd ..
fi

echo ""
echo "======================================"
echo "📡 API 호출 시작"
echo "======================================"
echo ""

# 2. API 호출
response=$(curl -X POST http://localhost:8080/api/stocks/update-from-api -s)
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"

echo ""
echo "⏳ 업데이트 진행 중... (약 3분 소요)"
echo "📋 실시간 로그:"
echo ""

# 3. 로그 모니터링
tail -f /tmp/spring-boot.log 2>/dev/null | grep --line-buffered "업데이트\|완료\|성공\|실패" &
TAIL_PID=$!

# 3분 대기
sleep 180

# tail 종료
kill $TAIL_PID 2>/dev/null

echo ""
echo "======================================"
echo "📊 최종 결과 확인"
echo "======================================"

# 4. 결과 확인
tail -50 /tmp/spring-boot.log | grep "업데이트 완료"

echo ""
echo "✅ 완료! 업데이트된 데이터 샘플:"
curl -s http://localhost:8080/api/stocks | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(f'\n총 {len(data)}개 종목')
if len(data) > 0:
    stock = data[0]
    print(f'\n샘플: {stock[\"name\"]} ({stock[\"id\"]})')
    if 'clpr' in stock and stock['clpr']:
        print(f'종가: {stock[\"clpr\"]:,}원')
        print(f'시가총액: {stock.get(\"mrktTotAmt\", 0):,}원')
    else:
        print('⚠️ 주가 데이터 없음 (API 키 확인 필요)')
" 2>/dev/null

echo ""
echo "======================================"
echo "🎉 작업 완료!"
echo "======================================"


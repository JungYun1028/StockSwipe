#!/bin/bash

# =====================================================
# 📊 StockSwipe 주식 데이터 자동 업데이트 스크립트
# =====================================================
# 
# 이 스크립트는:
# 1. secret.json에서 API 키 자동 로드
# 2. 백엔드가 실행 중인지 확인 (없으면 자동 시작)
# 3. 실제 주식 API를 호출하여 stocks 테이블 업데이트
# 4. 진행 상황 실시간 표시
#
# 사용법:
#   ./fetch-stock-data.sh
#
# =====================================================

set -e  # 에러 발생 시 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo "======================================"
echo -e "${CYAN}📊 StockSwipe 주식 데이터 업데이트${NC}"
echo "======================================"
echo ""

# =====================================================
# 1. 프로젝트 루트로 이동
# =====================================================
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo -e "${BLUE}📁 작업 디렉토리: $SCRIPT_DIR${NC}"
echo ""

# =====================================================
# 2. secret.json에서 API 키 로드
# =====================================================
echo -e "${YELLOW}🔑 API 키 확인 중...${NC}"

if [ ! -f "secret.json" ]; then
    echo -e "${RED}❌ secret.json 파일이 없습니다!${NC}"
    echo ""
    echo "다음 내용으로 secret.json 파일을 생성하세요:"
    echo ""
    echo '{'
    echo '  "stock_api": "YOUR_STOCK_API_KEY_HERE",'
    echo '  "openai_api_key": "YOUR_OPENAI_KEY_HERE"'
    echo '}'
    echo ""
    exit 1
fi

# Python으로 JSON 파싱
if command -v python3 &> /dev/null; then
    API_KEY=$(python3 -c "import json; print(json.load(open('secret.json'))['stock_api'])" 2>/dev/null)
else
    echo -e "${RED}❌ Python3가 설치되어 있지 않습니다!${NC}"
    exit 1
fi

if [ -z "$API_KEY" ] || [ "$API_KEY" = "" ]; then
    echo -e "${RED}❌ secret.json에 stock_api 키가 설정되지 않았습니다!${NC}"
    echo ""
    echo "secret.json 파일을 열어서 stock_api 값을 입력하세요."
    exit 1
fi

echo -e "${GREEN}✅ API 키 확인 완료: ${API_KEY:0:30}...${NC}"
export STOCK_API_KEY="$API_KEY"
echo ""

# =====================================================
# 3. 백엔드 실행 확인
# =====================================================
echo -e "${YELLOW}🔍 백엔드 서버 확인 중...${NC}"

if lsof -ti:8080 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 백엔드가 이미 실행 중입니다 (포트 8080)${NC}"
    BACKEND_ALREADY_RUNNING=true
else
    echo -e "${YELLOW}⚠️  백엔드가 실행되지 않았습니다.${NC}"
    echo -e "${BLUE}🚀 백엔드를 시작합니다...${NC}"
    echo ""
    
    # Java 21 경로 설정
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    fi
    
    # 백엔드 시작 (백그라운드)
    cd backend
    nohup mvn spring-boot:run > ../logs/stock-update-backend.log 2>&1 &
    BACKEND_PID=$!
    cd ..
    
    echo -e "${CYAN}⏳ 백엔드 시작 대기 중... (30초)${NC}"
    echo -e "${CYAN}   PID: $BACKEND_PID${NC}"
    
    # 30초 동안 1초마다 체크
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/stocks > /dev/null 2>&1; then
            echo ""
            echo -e "${GREEN}✅ 백엔드 시작 완료! (${i}초 소요)${NC}"
            break
        fi
        echo -n "."
        sleep 1
    done
    
    # 최종 확인
    if ! curl -s http://localhost:8080/api/stocks > /dev/null 2>&1; then
        echo ""
        echo -e "${RED}❌ 백엔드 시작 실패!${NC}"
        echo ""
        echo "로그 확인:"
        echo "  tail -50 logs/stock-update-backend.log"
        exit 1
    fi
    
    BACKEND_ALREADY_RUNNING=false
fi

echo ""

# =====================================================
# 4. 주식 데이터 업데이트 API 호출
# =====================================================
echo "======================================"
echo -e "${CYAN}📡 주식 데이터 업데이트 시작${NC}"
echo "======================================"
echo ""
echo -e "${YELLOW}⏳ API 호출 중... (약 3분 소요)${NC}"
echo -e "${YELLOW}   - 총 160개 종목${NC}"
echo -e "${YELLOW}   - 초당 10건 처리${NC}"
echo ""

# API 호출 (백그라운드)
response=$(curl -X POST http://localhost:8080/api/stocks/update-from-api \
    -H "Content-Type: application/json" \
    -s -w "\n%{http_code}" 2>&1)

http_code=$(echo "$response" | tail -n 1)
response_body=$(echo "$response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ 업데이트 요청 성공!${NC}"
    echo ""
    echo "응답:"
    echo "$response_body" | python3 -m json.tool 2>/dev/null || echo "$response_body"
else
    echo -e "${RED}❌ 업데이트 요청 실패 (HTTP $http_code)${NC}"
    echo "$response_body"
    exit 1
fi

echo ""
echo -e "${CYAN}📋 실시간 로그 모니터링 (Ctrl+C로 중단 가능):${NC}"
echo ""

# 로그 모니터링 (3분)
if [ -f "logs/stock-update-backend.log" ]; then
    timeout 180 tail -f logs/stock-update-backend.log 2>/dev/null | grep --line-buffered -E "업데이트|완료|성공|실패|✅|❌|⚠️" || true
fi

echo ""
echo "======================================"
echo -e "${CYAN}📊 업데이트 결과 확인${NC}"
echo "======================================"
echo ""

# =====================================================
# 5. 결과 확인
# =====================================================
stocks_json=$(curl -s http://localhost:8080/api/stocks)

if [ $? -eq 0 ]; then
    echo "$stocks_json" | python3 << 'EOF'
import sys, json

try:
    data = json.load(sys.stdin)
    total = len(data)
    
    # 데이터 있는 종목 수 확인
    with_price = sum(1 for s in data if s.get('clpr') is not None and s.get('clpr') != 0)
    
    print(f"총 종목 수: {total}개")
    print(f"주가 데이터 있음: {with_price}개")
    print(f"주가 데이터 없음: {total - with_price}개")
    print()
    
    if with_price > 0:
        print("✅ 업데이트 성공 샘플:")
        print()
        
        # 데이터 있는 종목 샘플 3개
        samples = [s for s in data if s.get('clpr') is not None and s.get('clpr') != 0][:3]
        
        for stock in samples:
            print(f"  종목명: {stock.get('name', 'N/A')} ({stock.get('id', 'N/A')})")
            print(f"  종가: {stock.get('clpr', 0):,}원")
            print(f"  등락률: {stock.get('fltRt', 0):.2f}%")
            print(f"  시가총액: {stock.get('mrktTotAmt', 0):,}원")
            print(f"  업데이트일: {stock.get('basDt', 'N/A')}")
            print()
    else:
        print("⚠️ 주가 데이터가 업데이트되지 않았습니다.")
        print("   API 키를 확인하세요: secret.json")
        
except Exception as e:
    print(f"❌ 데이터 파싱 오류: {e}")
    sys.exit(1)
EOF
else
    echo -e "${RED}❌ 종목 데이터 조회 실패${NC}"
fi

echo ""
echo "======================================"
echo -e "${GREEN}🎉 작업 완료!${NC}"
echo "======================================"
echo ""

# 백엔드를 이 스크립트가 시작했다면 안내
if [ "$BACKEND_ALREADY_RUNNING" = false ]; then
    echo -e "${YELLOW}💡 참고: 백엔드를 이 스크립트가 시작했습니다.${NC}"
    echo -e "${YELLOW}   백엔드는 계속 실행 중입니다 (포트 8080).${NC}"
    echo ""
    echo "   백엔드 종료 방법:"
    echo "     pkill -f 'spring-boot:run'"
    echo ""
fi

echo -e "${CYAN}다음 단계:${NC}"
echo "  1. 프론트엔드 실행: npm run dev"
echo "  2. 브라우저에서 http://localhost:5173 접속"
echo "  3. 카테고리 선택 후 종목 확인"
echo ""

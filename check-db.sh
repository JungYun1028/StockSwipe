#!/bin/bash

echo "🔍 StockSwipe 데이터베이스 확인 스크립트"
echo "=========================================="
echo ""

# PostgreSQL 경로
PSQL="/opt/homebrew/Cellar/postgresql@15/15.15_1/bin/psql"

# 1. 데이터베이스 존재 확인
echo "1️⃣  데이터베이스 확인..."
$PSQL postgres -c "\l" | grep stockswipe && echo "✅ stockswipe 데이터베이스 존재" || echo "❌ stockswipe 데이터베이스 없음"
echo ""

# 2. 백엔드 실행 확인
echo "2️⃣  백엔드 서버 확인..."
if curl -s http://localhost:8080/api/stocks > /dev/null 2>&1; then
    echo "✅ 백엔드 서버 실행 중 (http://localhost:8080)"
else
    echo "⏳ 백엔드 서버 시작 대기 중... (1분 정도 소요)"
fi
echo ""

# 3. 테이블 확인
echo "3️⃣  테이블 확인..."
$PSQL stockswipe -c "\dt" 2>/dev/null | grep -q "stocks" && echo "✅ stocks 테이블 생성됨" || echo "⏳ 테이블 생성 대기 중..."
echo ""

# 4. 데이터 확인
echo "4️⃣  주식 종목 데이터 확인..."
STOCK_COUNT=$($PSQL stockswipe -t -c "SELECT COUNT(*) FROM stocks;" 2>/dev/null | xargs)
if [ -n "$STOCK_COUNT" ] && [ "$STOCK_COUNT" -gt 0 ]; then
    echo "✅ 총 $STOCK_COUNT 개의 주식 종목 저장됨"
    echo ""
    echo "📊 카테고리별 종목 수:"
    $PSQL stockswipe -c "SELECT category, COUNT(*) as count FROM stock_categories GROUP BY category ORDER BY category;"
else
    echo "⏳ 데이터 삽입 대기 중..."
fi
echo ""

# 5. 샘플 데이터 조회
echo "5️⃣  샘플 데이터 (바이오 카테고리):"
$PSQL stockswipe -c "SELECT s.name, s.ticker FROM stocks s JOIN stock_categories sc ON s.id = sc.stock_id WHERE sc.category = '바이오' LIMIT 5;" 2>/dev/null || echo "⏳ 데이터 로딩 중..."
echo ""

echo "=========================================="
echo "💡 팁: 백엔드 로그 확인"
echo "   tail -f backend/logs/spring.log"
echo ""
echo "💡 API 테스트"
echo "   curl http://localhost:8080/api/stocks | jq"
echo "=========================================="


#!/bin/bash

echo "🤖 OpenAI를 사용하여 종목 정보를 생성합니다..."
echo ""
echo "⚠️  주의: 160개 종목 × 3개 요청 = 총 480회 API 호출"
echo "⚠️  API 호출 제한을 고려하여 각 종목마다 1초 대기합니다."
echo "⚠️  예상 소요 시간: 약 3분"
echo ""

read -p "계속하시겠습니까? (y/N): " confirm
if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
    echo "취소되었습니다."
    exit 0
fi

echo ""
echo "📊 API 호출 시작..."
response=$(curl -s -X POST http://localhost:8080/api/stocks/generate-ai-info)

echo "✅ 완료!"
echo "$response" | python3 -c "import sys, json; print(json.load(sys.stdin).get('message', 'Unknown'))"


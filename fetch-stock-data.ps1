# 📊 StockSwipe 주식 데이터 가져오기 스크립트 (Windows PowerShell)
# 오늘일자-1 기준으로 160개 종목 정보를 API로 받아와서 stock_prices 테이블에 저장합니다.

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📊 StockSwipe 주식 데이터 가져오기" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 백엔드가 실행 중인지 확인
Write-Host "1️⃣  백엔드 서비스 확인..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✅ 백엔드가 실행 중입니다." -ForegroundColor Green
} catch {
    Write-Host "❌ 백엔드가 실행 중이지 않습니다!" -ForegroundColor Red
    Write-Host "   백엔드를 먼저 시작해주세요: .\start-backend.ps1" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 2. 기준일자 확인 (오늘일자-1)
$yesterday = (Get-Date).AddDays(-1)
$basDt = $yesterday.ToString("yyyyMMdd")
Write-Host "2️⃣  기준일자 확인..." -ForegroundColor Yellow
Write-Host "   조회 기준일자: $basDt (오늘일자-1)" -ForegroundColor Gray
Write-Host ""

# 3. API 호출
Write-Host "3️⃣  주식 데이터 가져오기 시작..." -ForegroundColor Yellow
Write-Host "   총 160개 종목의 데이터를 가져옵니다." -ForegroundColor Gray
Write-Host "   (약 3~5분 소요, API 호출 제한으로 인해 종목당 0.1초 대기)" -ForegroundColor Gray
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/stocks/update-from-api" -Method POST -ContentType "application/json"
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "✅ API 호출 성공!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "응답:" -ForegroundColor Yellow
    $response | ConvertTo-Json -Depth 3
    Write-Host ""
    Write-Host "⏳ 데이터 업데이트 진행 중..." -ForegroundColor Yellow
    Write-Host "   백엔드 로그를 확인하세요: Get-Content backend\logs\spring.log -Tail 50 -Wait" -ForegroundColor Gray
    Write-Host ""
    Write-Host "💡 팁: 데이터 업데이트가 완료되면 stock_prices 테이블에 저장됩니다." -ForegroundColor Cyan
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "❌ API 호출 실패!" -ForegroundColor Red
    Write-Host "   오류 메시지: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "확인 사항:" -ForegroundColor Yellow
    Write-Host "   1. 백엔드가 실행 중인지 확인" -ForegroundColor Gray
    Write-Host "   2. API 키가 올바르게 설정되었는지 확인" -ForegroundColor Gray
    Write-Host "   3. 백엔드 로그 확인: Get-Content backend\logs\spring.log -Tail 50" -ForegroundColor Gray
    Write-Host ""
    exit 1
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

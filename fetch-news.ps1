# 📰 StockSwipe 뉴스 가져오기 스크립트 (Windows PowerShell)
# 구글 뉴스 RSS를 사용하여 모든 종목의 뉴스를 가져와서 DB에 저장합니다.

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📰 StockSwipe 뉴스 가져오기" -ForegroundColor Cyan
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

# 2. 뉴스 가져오기 옵션 선택
Write-Host "2️⃣  뉴스 가져오기 옵션 선택..." -ForegroundColor Yellow
Write-Host "   1) 특정 종목의 뉴스만 가져오기" -ForegroundColor Gray
Write-Host "   2) 모든 종목의 뉴스를 가져오기" -ForegroundColor Gray
Write-Host ""
$choice = Read-Host "선택 (1 또는 2, 기본값: 2)"

if ($choice -eq "1") {
    $stockId = Read-Host "종목코드를 입력하세요 (예: 005930)"
    
    if ([string]::IsNullOrWhiteSpace($stockId)) {
        Write-Host "❌ 종목코드를 입력하지 않았습니다." -ForegroundColor Red
        exit 1
    }
    
    Write-Host ""
    Write-Host "3️⃣  뉴스 가져오기 시작..." -ForegroundColor Yellow
    Write-Host "   종목코드: $stockId" -ForegroundColor Gray
    Write-Host ""
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/stocks/$stockId/fetch-news" -Method POST -ContentType "application/json"
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "✅ 뉴스 가져오기 성공!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "응답:" -ForegroundColor Yellow
        $response | ConvertTo-Json -Depth 3
        Write-Host ""
        
    } catch {
        Write-Host ""
        Write-Host "❌ 뉴스 가져오기 실패!" -ForegroundColor Red
        Write-Host "   오류 메시지: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        exit 1
    }
    
} else {
    Write-Host ""
    Write-Host "3️⃣  모든 종목의 뉴스 가져오기 시작..." -ForegroundColor Yellow
    Write-Host "   총 160개 종목의 뉴스를 가져옵니다." -ForegroundColor Gray
    Write-Host "   (약 5~10분 소요, API 호출 제한으로 인해 종목당 0.5초 대기)" -ForegroundColor Gray
    Write-Host ""
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/stocks/fetch-all-news" -Method POST -ContentType "application/json"
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "✅ 뉴스 가져오기 성공!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "응답:" -ForegroundColor Yellow
        $response | ConvertTo-Json -Depth 3
        Write-Host ""
        Write-Host "⏳ 뉴스 가져오기 진행 중..." -ForegroundColor Yellow
        Write-Host "   백엔드 로그를 확인하세요: Get-Content backend\logs\spring.log -Tail 50 -Wait" -ForegroundColor Gray
        Write-Host ""
        Write-Host "💡 팁: 뉴스 가져오기가 완료되면 news 테이블에 저장됩니다." -ForegroundColor Cyan
        Write-Host ""
        
    } catch {
        Write-Host ""
        Write-Host "❌ 뉴스 가져오기 실패!" -ForegroundColor Red
        Write-Host "   오류 메시지: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        Write-Host "확인 사항:" -ForegroundColor Yellow
        Write-Host "   1. 백엔드가 실행 중인지 확인" -ForegroundColor Gray
        Write-Host "   2. 백엔드 로그 확인: Get-Content backend\logs\spring.log -Tail 50" -ForegroundColor Gray
        Write-Host ""
        exit 1
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

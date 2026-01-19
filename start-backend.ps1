# 🚀 StockSwipe 백엔드 시작 스크립트 (Windows PowerShell)
# stock_master와 categories 테이블이 최초 1회 insert됩니다.

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🚀 StockSwipe 백엔드 시작" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. PostgreSQL 확인
Write-Host "1️⃣  PostgreSQL 서비스 확인..." -ForegroundColor Yellow
$postgresRunning = docker ps | Select-String "stockswipe-postgres"
if ($postgresRunning) {
    Write-Host "✅ PostgreSQL 실행 중" -ForegroundColor Green
} else {
    Write-Host "⚠️  PostgreSQL이 실행되지 않았습니다" -ForegroundColor Red
    Write-Host "   실행 명령: docker compose up -d" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 2. 환경 변수 설정
Write-Host "2️⃣  환경 변수 설정..." -ForegroundColor Yellow
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME","Machine")
Write-Host "✅ 환경 변수 설정 완료" -ForegroundColor Green
Write-Host ""

# 3. 백엔드 디렉토리로 이동
Write-Host "3️⃣  백엔드 디렉토리로 이동..." -ForegroundColor Yellow
Set-Location backend
Write-Host "✅ 현재 디렉토리: $(Get-Location)" -ForegroundColor Green
Write-Host ""

# 4. 로그 디렉토리 생성
Write-Host "4️⃣  로그 디렉토리 생성..." -ForegroundColor Yellow
if (-not (Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" | Out-Null
    Write-Host "✅ logs 디렉토리 생성 완료" -ForegroundColor Green
} else {
    Write-Host "✅ logs 디렉토리 이미 존재" -ForegroundColor Green
}
Write-Host ""

# 5. 백엔드 실행
Write-Host "5️⃣  Spring Boot 백엔드 시작..." -ForegroundColor Yellow
Write-Host "   (Maven 빌드 및 실행 - 약 1~2분 소요)" -ForegroundColor Gray
Write-Host "   stock_master와 categories 테이블이 최초 1회 자동으로 insert됩니다." -ForegroundColor Gray
Write-Host ""

# 백그라운드 실행 및 로그 저장
$mvnPath = "C:\Program Files\Apache\maven\bin\mvn.cmd"
Start-Process -FilePath $mvnPath -ArgumentList "spring-boot:run" -WorkingDirectory (Get-Location) -RedirectStandardOutput "logs\spring.log" -RedirectStandardError "logs\spring-error.log" -NoNewWindow

Write-Host "✅ 백엔드 시작됨 (백그라운드 실행)" -ForegroundColor Green
Write-Host ""

# 6. 시작 대기
Write-Host "6️⃣  서버 시작 대기 중..." -ForegroundColor Yellow
$maxAttempts = 60
$attempt = 0
$started = $false

while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks" -Method GET -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host ""
            Write-Host "✅ 백엔드 서버 시작 완료!" -ForegroundColor Green
            Write-Host ""
            $started = $true
            break
        }
    } catch {
        # 서버가 아직 시작되지 않음
    }
    
    Write-Host -NoNewline "."
    Start-Sleep -Seconds 1
    $attempt++
}

if (-not $started) {
    Write-Host ""
    Write-Host "⚠️  서버 시작 시간 초과 (60초)" -ForegroundColor Red
    Write-Host "   로그 확인: Get-Content logs\spring.log -Tail 50" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🎉 서버 정보" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📡 API: http://localhost:8080" -ForegroundColor White
Write-Host "📊 종목 조회: http://localhost:8080/api/stocks" -ForegroundColor White
Write-Host "📁 카테고리: http://localhost:8080/api/categories" -ForegroundColor White
Write-Host ""
Write-Host "📋 로그 확인: Get-Content logs\spring.log -Tail 50 -Wait" -ForegroundColor Yellow
Write-Host "🛑 서버 중지: Get-Process | Where-Object {`$_.ProcessName -like '*java*'} | Stop-Process" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 원래 디렉토리로 복귀
Set-Location ..

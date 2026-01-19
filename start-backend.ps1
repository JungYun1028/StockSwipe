# 🚀 StockSwipe 백엔드 시작 스크립트 (Windows PowerShell)
# stock_master와 categories 테이블이 최초 1회 insert됩니다.

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🚀 StockSwipe 백엔드 시작" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 환경 변수 설정
Write-Host "1️⃣  환경 변수 설정..." -ForegroundColor Yellow
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME","Machine")
Write-Host "✅ 환경 변수 설정 완료" -ForegroundColor Green
Write-Host ""

# 2. API 키 로드 (secret.json)
Write-Host "2️⃣  API 키 로드..." -ForegroundColor Yellow
$secretPath = Join-Path (Get-Location) "secret.json"
if (Test-Path $secretPath) {
    $rawSecret = Get-Content $secretPath -Raw
    $parsed = $false

    try {
        $secret = $rawSecret | ConvertFrom-Json
        if ($secret.stock_api) { $env:STOCK_API_KEY = $secret.stock_api }
        if ($secret.openai_api_key) { $env:OPENAI_API_KEY = $secret.openai_api_key }
        $parsed = $true
    } catch {
        # JSON 파싱 실패 시 regex로 키를 추출 (start-backend.sh 방식과 동일한 방향)
        $parsed = $false
    }

    if (-not $parsed -or -not $env:OPENAI_API_KEY -or -not $env:STOCK_API_KEY) {
        $stockMatch = [regex]::Match($rawSecret, '"stock_api"\s*:\s*"([^"]*)"')
        if ($stockMatch.Success) { $env:STOCK_API_KEY = $stockMatch.Groups[1].Value }

        $openaiMatch = [regex]::Match($rawSecret, '"openai_api_key"\s*:\s*"([^"]*)"')
        if ($openaiMatch.Success) { $env:OPENAI_API_KEY = $openaiMatch.Groups[1].Value }
    }

    if (-not $env:OPENAI_API_KEY) {
        Write-Host "⚠️  openai_api_key 값이 비어 있습니다." -ForegroundColor Yellow
    } else {
        $maskedOpenAiKey = $env:OPENAI_API_KEY.Substring(0, [Math]::Min(6, $env:OPENAI_API_KEY.Length)) + "..."
        Write-Host "✅ OpenAI 키 로드됨: $maskedOpenAiKey (len=$($env:OPENAI_API_KEY.Length))" -ForegroundColor Green
    }
    if (-not $env:STOCK_API_KEY) {
        Write-Host "⚠️  stock_api 값이 비어 있습니다." -ForegroundColor Yellow
    }

    Write-Host "✅ secret.json에서 API 키를 로드했습니다." -ForegroundColor Green
} else {
    Write-Host "⚠️  secret.json 파일을 찾을 수 없습니다." -ForegroundColor Yellow
}
Write-Host ""

# 3. PostgreSQL 확인
Write-Host "3️⃣  PostgreSQL 서비스 확인..." -ForegroundColor Yellow
$dockerCmd = Get-Command docker -ErrorAction SilentlyContinue
if (-not $dockerCmd) {
    Write-Host "❌ Docker CLI를 찾을 수 없습니다." -ForegroundColor Red
    Write-Host "   Docker Desktop 설치 후 새 터미널에서 다시 실행하세요." -ForegroundColor Yellow
    exit 1
}

$postgresRunning = docker ps | Select-String "stockswipe-postgres"
if ($postgresRunning) {
    Write-Host "✅ PostgreSQL 실행 중" -ForegroundColor Green
} else {
    Write-Host "⚠️  PostgreSQL이 실행되지 않았습니다" -ForegroundColor Red
    Write-Host "   실행 명령: docker compose up -d" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 4. 백엔드 디렉토리로 이동
Write-Host "4️⃣  백엔드 디렉토리로 이동..." -ForegroundColor Yellow
Set-Location backend
Write-Host "✅ 현재 디렉토리: $(Get-Location)" -ForegroundColor Green
Write-Host ""

# 5. 로그 디렉토리 생성
Write-Host "5️⃣  로그 디렉토리 생성..." -ForegroundColor Yellow
if (-not (Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" | Out-Null
    Write-Host "✅ logs 디렉토리 생성 완료" -ForegroundColor Green
} else {
    Write-Host "✅ logs 디렉토리 이미 존재" -ForegroundColor Green
}
Write-Host ""

# 6. 백엔드 실행
Write-Host "6️⃣  Spring Boot 백엔드 시작..." -ForegroundColor Yellow
Write-Host "   (Maven 빌드 및 실행 - 약 1~2분 소요)" -ForegroundColor Gray
Write-Host "   stock_master와 categories 테이블이 최초 1회 자동으로 insert됩니다." -ForegroundColor Gray
Write-Host ""

# 백그라운드 실행 및 로그 저장
$mvnPath = $null
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCmd) {
    $mvnPath = $mvnCmd.Source
} elseif ($env:MAVEN_HOME) {
    $candidate = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
    if (Test-Path $candidate) { $mvnPath = $candidate }
} elseif ($env:M2_HOME) {
    $candidate = Join-Path $env:M2_HOME "bin\mvn.cmd"
    if (Test-Path $candidate) { $mvnPath = $candidate }
} else {
    $candidate = "C:\Program Files\Apache\maven\bin\mvn.cmd"
    if (Test-Path $candidate) { $mvnPath = $candidate }
}

if (-not $mvnPath) {
    Write-Host "❌ Maven(mvn) 실행 파일을 찾을 수 없습니다." -ForegroundColor Red
    Write-Host "   Maven 설치 후 PATH에 추가하거나 MAVEN_HOME/M2_HOME을 설정하세요." -ForegroundColor Yellow
    exit 1
}

# OPENAI/주식 API 키가 자식 프로세스로 확실히 전달되도록 cmd를 통해 실행
$cmd = @(
    "set OPENAI_API_KEY=$($env:OPENAI_API_KEY)",
    "set STOCK_API_KEY=$($env:STOCK_API_KEY)",
    "`"$mvnPath`" spring-boot:run"
) -join " && "
Start-Process -FilePath "cmd.exe" -ArgumentList "/c $cmd" -WorkingDirectory (Get-Location) -RedirectStandardOutput "logs\spring.log" -RedirectStandardError "logs\spring-error.log" -NoNewWindow

Write-Host "✅ 백엔드 시작됨 (백그라운드 실행)" -ForegroundColor Green
Write-Host ""

# 7. 시작 대기
Write-Host "7️⃣  서버 시작 대기 중..." -ForegroundColor Yellow
$maxAttempts = 60
$attempt = 0
$started = $false

while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/stocks" -Method GET -TimeoutSec 2 -ErrorAction SilentlyContinue -UseBasicParsing
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

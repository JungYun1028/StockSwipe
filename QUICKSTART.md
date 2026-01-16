# StockSwipe 빠른 시작 가이드 🚀

팀원들을 위한 최소 설정으로 바로 실행하는 방법입니다.

## ⚠️ 필수 요구사항

### 1. Java 21 설치 (**중요: 17이 아닌 21!**)

#### macOS
```bash
# Homebrew로 설치
brew install openjdk@21

# JAVA_HOME 설정 (필수!)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# 확인
java -version  # "21.0.x" 출력되어야 함
```

#### Windows
1. [Eclipse Temurin 21 다운로드](https://adoptium.net/temurin/releases/?version=21)
2. 설치 시 "Set JAVA_HOME" 옵션 체크
3. CMD에서 확인: `java -version`

#### Linux
```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

### 2. Maven 설치

#### macOS
```bash
brew install maven
mvn -version  # 확인
```

#### Windows
1. [Maven 다운로드](https://maven.apache.org/download.cgi)
2. 압축 해제 후 환경변수 PATH에 추가
3. CMD에서 확인: `mvn -version`

#### Linux
```bash
sudo apt install maven
mvn -version
```

### 3. PostgreSQL 설치 및 실행

#### macOS
```bash
# 설치
brew install postgresql@15

# 서비스 시작
brew services start postgresql@15

# 데이터베이스 생성
createdb stockswipe

# 확인
psql stockswipe -c "SELECT version();"
```

#### Windows
1. [PostgreSQL 다운로드](https://www.postgresql.org/download/windows/)
2. 설치 시 비밀번호: `postgres`
3. pgAdmin 또는 psql로 데이터베이스 생성:
```sql
CREATE DATABASE stockswipe;
```

#### Linux
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# 데이터베이스 생성
sudo -u postgres createdb stockswipe
```

### 4. Node.js 18+ 설치

#### macOS
```bash
brew install node@18
node -v  # v18.x.x 이상 확인
```

#### Windows/Linux
[Node.js 공식 사이트](https://nodejs.org/)에서 LTS 버전 다운로드

## 🔑 API 키 설정 (선택사항)

백엔드가 실행되려면 `secret.json` 파일이 필요합니다.

프로젝트 루트에 `secret.json` 파일 생성:

```json
{
  "stock_api": "YOUR_STOCK_API_KEY_HERE",
  "openai_api_key": "YOUR_OPENAI_API_KEY_HERE"
}
```

**임시로 실행만 하려면** 빈 값으로 생성:

```bash
# macOS/Linux
cat > secret.json << 'EOF'
{
  "stock_api": "",
  "openai_api_key": ""
}
EOF

# Windows (PowerShell)
@"
{
  "stock_api": "",
  "openai_api_key": ""
}
"@ | Out-File -Encoding UTF8 secret.json
```

## ⚡ 실행 방법

### 방법 1: 자동 스크립트 (macOS/Linux 추천)

```bash
# 1. 프로젝트 클론
git clone <repository-url>
cd stockswipe

# 2. 백엔드 실행 (자동으로 모든 설정 완료)
chmod +x start-backend.sh
./start-backend.sh

# 3. 새 터미널에서 프론트엔드 실행
npm install
npm run dev
```

### 방법 2: 수동 실행 (Windows 또는 문제 발생 시)

#### 백엔드 실행
```bash
cd backend

# PostgreSQL 실행 확인 (Windows는 서비스에서 확인)
# macOS: brew services list | grep postgresql
# Linux: sudo systemctl status postgresql

# Maven으로 실행
mvn spring-boot:run
```

**백엔드 실행 확인:**
- 브라우저에서 http://localhost:8080/api/stocks 접속
- JSON 데이터가 보이면 성공!

#### 프론트엔드 실행
```bash
# 프로젝트 루트로 돌아가서
cd ..

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

**프론트엔드 접속:**
- 브라우저에서 http://localhost:5173 (또는 8000)

## 🐛 문제 해결

### "Java version mismatch" 에러
```bash
# JAVA_HOME 확인
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME%  # Windows

# Java 21로 설정
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
```

### "PostgreSQL connection refused" 에러
```bash
# PostgreSQL 실행 확인
# macOS
brew services start postgresql@15

# Linux
sudo systemctl start postgresql

# Windows: 서비스에서 PostgreSQL 시작
```

### "Port 8080 already in use" 에러
```bash
# 8080 포트 사용 중인 프로세스 종료
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID번호> /F
```

### Maven 빌드 실패
```bash
# Maven 캐시 클리어
cd backend
mvn clean install -U

# 또는 캐시 삭제 후 재시도
rm -rf ~/.m2/repository
mvn clean package
```

### 종목 정보가 안 보임 (500 에러)
1. **백엔드가 실행 중인지 확인**: http://localhost:8080/api/stocks
2. **PostgreSQL이 실행 중인지 확인**
3. **데이터베이스 `stockswipe`가 생성되었는지 확인**
4. **백엔드 로그 확인**: `backend/logs/spring.log`

## 📊 실행 확인

백엔드와 프론트엔드가 모두 실행되면:

1. http://localhost:5173 접속
2. 카테고리 선택 (바이오, AI 등)
3. 종목 카드가 보이면 성공! 🎉
4. 좌우 스와이프로 종목 탐색

## 💡 개발 팁

### 백그라운드 실행
```bash
# 백엔드를 백그라운드로 실행 (macOS/Linux)
cd backend
nohup mvn spring-boot:run > logs/spring.log 2>&1 &

# 프론트엔드
npm run dev
```

### 프로세스 종료
```bash
# 백엔드 종료
pkill -f "spring-boot:run"

# 프론트엔드: Ctrl+C
```

## 📞 도움이 필요하면

1. **로그 확인**: `backend/logs/spring.log`
2. **브라우저 콘솔 확인**: F12 → Console 탭
3. **이슈 생성** 또는 팀원에게 문의

---

**처음 실행하는 데 약 10-15분 소요됩니다** (Java, Maven, PostgreSQL 설치 포함)

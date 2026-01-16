# StockSwipe 📈

> 스와이프로 발견하는 나만의 투자 종목

주식 종목 탐색을 **검색 중심·지표 중심**에서 벗어나 **스와이프 인터랙션**으로 직관화한 MVP입니다.

## 🔄 프로젝트 변환 완료

**TypeScript → JavaScript + Java Spring Boot**로 완전히 변환되었습니다!
- ✅ 프론트엔드: React (JavaScript)
- ✅ 백엔드: Java Spring Boot
- ✅ 데이터베이스: H2 (개발용), RDS 연동 준비 완료
- ✅ API: RESTful API 구현 완료

**상세 설치 및 실행 가이드는 [SETUP.md](./SETUP.md)를 참고하세요.**

## ✨ 주요 기능

### 🎯 카테고리 기반 추천
- 바이오, AI, 선박, 식품, 에너지, 반도체, 금융, 2차전지 등 관심 섹터 선택
- 선택한 카테고리 기반으로 종목 가중치 자동 설정

### 👆 스와이프 탐색
- **오른쪽 스와이프 (Like)**: 관심 종목에 저장, 유사 종목 추천 강화
- **왼쪽 스와이프 (Pass)**: 유사 종목 노출 빈도 감소
- 하단 미니 카드로 다음 추천 종목 미리보기

### 📊 종목 카드
- 종목명, 티커, 일간 변동 차트
- 현재가, 고가/저가, 거래량, 매수/매도량
- 연관 뉴스 3개 요약

### 📱 상세 페이지
- **종목 상세**: 기업 개요, 사업 내용, 기술적 지표(RSI, 이동평균), 전문가 분석
- **뉴스 상세**: AI 한 줄 요약, 키워드 자동 추출, 관련 종목 연결

### 💬 AI 챗봇 (플로팅)
- 현재 페이지 컨텍스트 자동 인식
- 용어 설명, 지표 해석, 투자 관련 질의응답
- OpenAI API 연동 구조 (현재 Mock 응답)

### 💝 관심 종목 리스트
- Like한 종목 자동 저장
- 섹터별 그룹화
- 관심 키워드 태그 자동 생성

## 🛠 기술 스택

### 프론트엔드
- **프레임워크**: React 19 + JavaScript + Vite
- **스타일링**: CSS Modules
- **상태관리**: React Context API
- **HTTP 클라이언트**: Axios
- **스와이프**: react-swipeable
- **차트**: Recharts
- **애니메이션**: Framer Motion
- **아이콘**: Lucide React

### 백엔드
- **프레임워크**: Spring Boot 3.2.1
- **언어**: Java 21 ⚠️
- **빌드 도구**: Maven 3.9+
- **ORM**: Spring Data JPA
- **데이터베이스**: PostgreSQL 15 (개발), RDS (프로덕션)
- **API**: RESTful API

## 🎨 디자인 시스템

### 컬러 팔레트
- **배경**: `#0E0F12` (Charcoal Black)
- **카드**: `#1A1C22`
- **상승/포인트**: `#C64A3A` (Deep Red)
- **하락**: `#4A6FA5` (Gray Blue)

### UI 특징
- 다크 테마 기반
- 둥근 모서리 (22px) 버블 카드
- 그림자로 부유하는 느낌
- Like 시 카드 가장자리 레드 라인 효과

## 🚀 실행 방법

### ⚡ 빠른 시작 (처음 실행하는 팀원)

**필수 요구사항:**
- Java 21 (⚠️ 중요: 17이 아님!)
- Maven 3.9+
- PostgreSQL 15+
- Node.js 18+

**[QUICKSTART.md](./QUICKSTART.md) ← 여기를 먼저 보세요!**

### 📦 간단 실행 (환경 설정 완료된 경우)

macOS/Linux:
```bash
# 백엔드 (자동 스크립트)
./start-backend.sh

# 프론트엔드 (새 터미널)
npm install && npm run dev
```

Windows:
```bash
# 백엔드
cd backend
mvn spring-boot:run

# 프론트엔드 (새 CMD)
npm install
npm run dev
```

**접속:**
- 프론트엔드: http://localhost:5173 (또는 8000)
- 백엔드 API: http://localhost:8080/api/stocks

**상세 가이드:**
- 처음 실행: [QUICKSTART.md](./QUICKSTART.md) ⭐
- 전체 설정: [SETUP.md](./SETUP.md)

## 📁 프로젝트 구조

```
src/
├── components/          # 재사용 컴포넌트
│   ├── FloatingChatbot  # AI 챗봇
│   ├── MiniChart        # 미니 차트
│   ├── Navigation       # 하단 네비게이션
│   ├── PreviewCards     # 다음 종목 미리보기
│   ├── StockCard        # 종목 카드
│   └── SwipeContainer   # 스와이프 컨테이너
├── context/
│   └── AppContext       # 전역 상태 관리
├── data/
│   └── mockStocks       # Mock 데이터
├── pages/               # 페이지 컴포넌트
│   ├── CategorySelect   # 카테고리 선택
│   ├── Home             # 홈 (스와이프 피드)
│   ├── NewsDetail       # 뉴스 상세
│   ├── StockDetail      # 종목 상세
│   └── WatchList        # 관심 종목
└── App.tsx              # 라우팅
```

## 🔮 향후 개선 사항

- [ ] 실제 주식 데이터 API 연동 (한국투자증권, 키움 등)
- [ ] 네이버 뉴스 API 연동
- [ ] OpenAI API 실제 연동
- [ ] 사용자 인증 및 데이터 영속화
- [ ] 푸시 알림 (관심 종목 변동)
- [ ] 포트폴리오 시뮬레이션

## 📜 라이선스

MIT License

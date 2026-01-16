-- =====================================================
-- StockSwipe 초기 데이터 SQL
-- =====================================================
-- 
-- 사용법:
--   psql -d stockswipe -f init-data.sql
--
-- 또는 PostgreSQL 클라이언트에서 직접 실행
-- =====================================================

-- 기존 데이터 삭제 (필요시)
-- DELETE FROM stocks;
-- DELETE FROM categories;

-- =====================================================
-- 1. CATEGORIES 테이블 - 8개 카테고리
-- =====================================================

INSERT INTO categories (code, name) VALUES
('bio', '바이오'),
('ai', 'AI'),
('ship', '선박'),
('food', '식품'),
('energy', '에너지'),
('semi', '반도체'),
('finance', '금융'),
('battery', '2차전지')
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- 2. STOCKS 테이블 - 160개 종목
-- =====================================================

-- 바이오 종목 (20개) - category_id = 1
INSERT INTO stocks (stock_id, name, category_id) VALUES
('068270', '셀트리온', (SELECT id FROM categories WHERE code = 'bio')),
('207940', '삼성바이오로직스', (SELECT id FROM categories WHERE code = 'bio')),
('091990', '셀트리온헬스케어', (SELECT id FROM categories WHERE code = 'bio')),
('326030', 'SK바이오팜', (SELECT id FROM categories WHERE code = 'bio')),
('000100', '유한양행', (SELECT id FROM categories WHERE code = 'bio')),
('128940', '한미약품', (SELECT id FROM categories WHERE code = 'bio')),
('069620', '대웅제약', (SELECT id FROM categories WHERE code = 'bio')),
('185750', '종근당', (SELECT id FROM categories WHERE code = 'bio')),
('006280', '녹십자', (SELECT id FROM categories WHERE code = 'bio')),
('001060', 'JW중외제약', (SELECT id FROM categories WHERE code = 'bio')),
('019170', '신풍제약', (SELECT id FROM categories WHERE code = 'bio')),
('007570', '일양약품', (SELECT id FROM categories WHERE code = 'bio')),
('170900', '동아에스티', (SELECT id FROM categories WHERE code = 'bio')),
('003000', '부광약품', (SELECT id FROM categories WHERE code = 'bio')),
('195940', 'HK이노엔', (SELECT id FROM categories WHERE code = 'bio')),
('263050', '유틸렉스', (SELECT id FROM categories WHERE code = 'bio')),
('140410', '메지온', (SELECT id FROM categories WHERE code = 'bio')),
('085660', '차바이오텍', (SELECT id FROM categories WHERE code = 'bio')),
('064550', '바이오니아', (SELECT id FROM categories WHERE code = 'bio')),
('095700', '제넥신', (SELECT id FROM categories WHERE code = 'bio'))
ON CONFLICT (stock_id) DO NOTHING;

-- AI 종목 (20개) - category_id = 2
INSERT INTO stocks (stock_id, name, category_id) VALUES
('035420', '네이버', (SELECT id FROM categories WHERE code = 'ai')),
('035720', '카카오', (SELECT id FROM categories WHERE code = 'ai')),
('066570', 'LG전자', (SELECT id FROM categories WHERE code = 'ai')),
('108860', '셀바스AI', (SELECT id FROM categories WHERE code = 'ai')),
('304100', '솔트룩스', (SELECT id FROM categories WHERE code = 'ai')),
('030520', '한글과컴퓨터', (SELECT id FROM categories WHERE code = 'ai')),
('368770', '수아랩', (SELECT id FROM categories WHERE code = 'ai')),
('330350', '씨앤에이아이', (SELECT id FROM categories WHERE code = 'ai')),
('315640', '딥노이드', (SELECT id FROM categories WHERE code = 'ai')),
('100120', '뷰웍스', (SELECT id FROM categories WHERE code = 'ai')),
('189300', '인텔리안테크', (SELECT id FROM categories WHERE code = 'ai')),
('226340', '코난테크놀로지', (SELECT id FROM categories WHERE code = 'ai')),
('036570', '엔씨소프트', (SELECT id FROM categories WHERE code = 'ai')),
('251270', '넷마블', (SELECT id FROM categories WHERE code = 'ai')),
('259960', '크래프톤', (SELECT id FROM categories WHERE code = 'ai')),
('293490', '카카오게임즈', (SELECT id FROM categories WHERE code = 'ai')),
('323410', '카카오뱅크', (SELECT id FROM categories WHERE code = 'ai')),
('112040', '위메이드', (SELECT id FROM categories WHERE code = 'ai')),
('078340', '컴투스', (SELECT id FROM categories WHERE code = 'ai')),
('181710', 'NHN', (SELECT id FROM categories WHERE code = 'ai'))
ON CONFLICT (stock_id) DO NOTHING;

-- 선박 종목 (20개) - category_id = 3
INSERT INTO stocks (stock_id, name, category_id) VALUES
('009540', 'HD한국조선해양', (SELECT id FROM categories WHERE code = 'ship')),
('010140', '삼성중공업', (SELECT id FROM categories WHERE code = 'ship')),
('042660', '한화오션', (SELECT id FROM categories WHERE code = 'ship')),
('010620', '현대미포조선', (SELECT id FROM categories WHERE code = 'ship')),
('000430', '대한조선', (SELECT id FROM categories WHERE code = 'ship')),
('103590', 'HJ중공업', (SELECT id FROM categories WHERE code = 'ship')),
('000070', '삼양홀딩스', (SELECT id FROM categories WHERE code = 'ship')),
('028670', '팬오션', (SELECT id FROM categories WHERE code = 'ship')),
('003280', '흥아해운', (SELECT id FROM categories WHERE code = 'ship')),
('005880', '대한해운', (SELECT id FROM categories WHERE code = 'ship')),
('044450', 'KSS해운', (SELECT id FROM categories WHERE code = 'ship')),
('000540', '고려해운', (SELECT id FROM categories WHERE code = 'ship')),
('019570', 'SM상선', (SELECT id FROM categories WHERE code = 'ship')),
('000120', 'CJ대한통운', (SELECT id FROM categories WHERE code = 'ship')),
('086280', '현대글로비스', (SELECT id FROM categories WHERE code = 'ship')),
('002320', '한진', (SELECT id FROM categories WHERE code = 'ship')),
('180640', '한진칼', (SELECT id FROM categories WHERE code = 'ship')),
('025320', '시노펙스', (SELECT id FROM categories WHERE code = 'ship')),
('004270', '남성', (SELECT id FROM categories WHERE code = 'ship')),
('001430', '대선조선', (SELECT id FROM categories WHERE code = 'ship'))
ON CONFLICT (stock_id) DO NOTHING;

-- 식품 종목 (20개) - category_id = 4
INSERT INTO stocks (stock_id, name, category_id) VALUES
('097950', 'CJ제일제당', (SELECT id FROM categories WHERE code = 'food')),
('007310', '오뚜기', (SELECT id FROM categories WHERE code = 'food')),
('004370', '농심', (SELECT id FROM categories WHERE code = 'food')),
('280360', '롯데제과', (SELECT id FROM categories WHERE code = 'food')),
('271560', '오리온', (SELECT id FROM categories WHERE code = 'food')),
('049770', '동원F&B', (SELECT id FROM categories WHERE code = 'food')),
('003380', '하림지주', (SELECT id FROM categories WHERE code = 'food')),
('003960', '사조대림', (SELECT id FROM categories WHERE code = 'food')),
('001680', '대상', (SELECT id FROM categories WHERE code = 'food')),
('003230', '삼양식품', (SELECT id FROM categories WHERE code = 'food')),
('005180', '빙그레', (SELECT id FROM categories WHERE code = 'food')),
('003920', '남양유업', (SELECT id FROM categories WHERE code = 'food')),
('267980', '매일유업', (SELECT id FROM categories WHERE code = 'food')),
('026960', '동서', (SELECT id FROM categories WHERE code = 'food')),
('005610', 'SPC삼립', (SELECT id FROM categories WHERE code = 'food')),
('008040', '파리크라상', (SELECT id FROM categories WHERE code = 'food')),
('007980', 'CJ푸드빌', (SELECT id FROM categories WHERE code = 'food')),
('031440', '신세계푸드', (SELECT id FROM categories WHERE code = 'food')),
('017810', '풀무원', (SELECT id FROM categories WHERE code = 'food')),
('004000', '한국야쿠르트', (SELECT id FROM categories WHERE code = 'food'))
ON CONFLICT (stock_id) DO NOTHING;

-- 에너지 종목 (20개) - category_id = 5
INSERT INTO stocks (stock_id, name, category_id) VALUES
('015760', '한국전력', (SELECT id FROM categories WHERE code = 'energy')),
('096770', 'SK이노베이션', (SELECT id FROM categories WHERE code = 'energy')),
('078930', 'GS', (SELECT id FROM categories WHERE code = 'energy')),
('010950', 'S-Oil', (SELECT id FROM categories WHERE code = 'energy')),
('000880', '한화', (SELECT id FROM categories WHERE code = 'energy')),
('034020', '두산에너빌리티', (SELECT id FROM categories WHERE code = 'energy')),
('036460', '한국가스공사', (SELECT id FROM categories WHERE code = 'energy')),
('001250', 'GS글로벌', (SELECT id FROM categories WHERE code = 'energy')),
('018670', 'SK가스', (SELECT id FROM categories WHERE code = 'energy')),
('017940', 'E1', (SELECT id FROM categories WHERE code = 'energy')),
('012450', '한화에어로스페이스', (SELECT id FROM categories WHERE code = 'energy')),
('336260', '두산퓨얼셀', (SELECT id FROM categories WHERE code = 'energy')),
('288620', '에스퓨얼셀', (SELECT id FROM categories WHERE code = 'energy')),
('200470', '블룸비츠', (SELECT id FROM categories WHERE code = 'energy')),
('011930', '신성이엔지', (SELECT id FROM categories WHERE code = 'energy')),
('052690', '한국전력기술', (SELECT id FROM categories WHERE code = 'energy')),
('051600', '한전KPS', (SELECT id FROM categories WHERE code = 'energy')),
('006360', 'GS건설', (SELECT id FROM categories WHERE code = 'energy')),
('000720', '현대건설', (SELECT id FROM categories WHERE code = 'energy')),
('000210', '대림산업', (SELECT id FROM categories WHERE code = 'energy'))
ON CONFLICT (stock_id) DO NOTHING;

-- 반도체 종목 (20개) - category_id = 6
INSERT INTO stocks (stock_id, name, category_id) VALUES
('005930', '삼성전자', (SELECT id FROM categories WHERE code = 'semi')),
('000660', 'SK하이닉스', (SELECT id FROM categories WHERE code = 'semi')),
('000990', 'DB하이텍', (SELECT id FROM categories WHERE code = 'semi')),
('042700', '한미반도체', (SELECT id FROM categories WHERE code = 'semi')),
('240810', '원익IPS', (SELECT id FROM categories WHERE code = 'semi')),
('084370', '유진테크', (SELECT id FROM categories WHERE code = 'semi')),
('036930', '주성엔지니어링', (SELECT id FROM categories WHERE code = 'semi')),
('265520', 'AP시스템', (SELECT id FROM categories WHERE code = 'semi')),
('095610', '테스', (SELECT id FROM categories WHERE code = 'semi')),
('281820', '케이씨텍', (SELECT id FROM categories WHERE code = 'semi')),
('140860', '파크시스템스', (SELECT id FROM categories WHERE code = 'semi')),
('039030', '이오테크닉스', (SELECT id FROM categories WHERE code = 'semi')),
('074600', '원익QnC', (SELECT id FROM categories WHERE code = 'semi')),
('036540', 'SFA반도체', (SELECT id FROM categories WHERE code = 'semi')),
('064760', '티씨케이', (SELECT id FROM categories WHERE code = 'semi')),
('108320', 'LX세미콘', (SELECT id FROM categories WHERE code = 'semi')),
('061970', '엘비세미콘', (SELECT id FROM categories WHERE code = 'semi')),
('067310', '하나마이크론', (SELECT id FROM categories WHERE code = 'semi')),
('033640', '네패스', (SELECT id FROM categories WHERE code = 'semi')),
('222800', 'SK실트론', (SELECT id FROM categories WHERE code = 'semi'))
ON CONFLICT (stock_id) DO NOTHING;

-- 금융 종목 (20개) - category_id = 7
INSERT INTO stocks (stock_id, name, category_id) VALUES
('032830', '삼성생명', (SELECT id FROM categories WHERE code = 'finance')),
('105560', 'KB금융', (SELECT id FROM categories WHERE code = 'finance')),
('055550', '신한지주', (SELECT id FROM categories WHERE code = 'finance')),
('086790', '하나금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('316140', '우리금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('175330', 'JB금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('139130', 'DGB금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('138930', 'BNK금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('000810', '삼성화재', (SELECT id FROM categories WHERE code = 'finance')),
('005830', 'DB손해보험', (SELECT id FROM categories WHERE code = 'finance')),
('088350', '한화생명', (SELECT id FROM categories WHERE code = 'finance')),
('006800', '미래에셋증권', (SELECT id FROM categories WHERE code = 'finance')),
('016360', '삼성증권', (SELECT id FROM categories WHERE code = 'finance')),
('003470', '한국투자증권', (SELECT id FROM categories WHERE code = 'finance')),
('005940', 'NH투자증권', (SELECT id FROM categories WHERE code = 'finance')),
('039490', '키움증권', (SELECT id FROM categories WHERE code = 'finance')),
('071050', '한국금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('138040', '메리츠금융지주', (SELECT id FROM categories WHERE code = 'finance')),
('030610', '교보증권', (SELECT id FROM categories WHERE code = 'finance')),
('003560', '하이투자증권', (SELECT id FROM categories WHERE code = 'finance'))
ON CONFLICT (stock_id) DO NOTHING;

-- 2차전지 종목 (20개) - category_id = 8
INSERT INTO stocks (stock_id, name, category_id) VALUES
('373220', 'LG에너지솔루션', (SELECT id FROM categories WHERE code = 'battery')),
('006400', '삼성SDI', (SELECT id FROM categories WHERE code = 'battery')),
('402340', 'SK온', (SELECT id FROM categories WHERE code = 'battery')),
('003670', '포스코퓨처엠', (SELECT id FROM categories WHERE code = 'battery')),
('247540', '에코프로비엠', (SELECT id FROM categories WHERE code = 'battery')),
('086520', '에코프로', (SELECT id FROM categories WHERE code = 'battery')),
('066970', 'L&F', (SELECT id FROM categories WHERE code = 'battery')),
('278280', '천보', (SELECT id FROM categories WHERE code = 'battery')),
('005070', '코스모신소재', (SELECT id FROM categories WHERE code = 'battery')),
('137400', '피엔티', (SELECT id FROM categories WHERE code = 'battery')),
('078600', '대주전자재료', (SELECT id FROM categories WHERE code = 'battery')),
('093370', '후성', (SELECT id FROM categories WHERE code = 'battery')),
('336370', '솔루스첨단소재', (SELECT id FROM categories WHERE code = 'battery')),
('020150', '일진머티리얼즈', (SELECT id FROM categories WHERE code = 'battery')),
('361610', 'SK아이이테크놀로지', (SELECT id FROM categories WHERE code = 'battery')),
('089980', '상아프론테크', (SELECT id FROM categories WHERE code = 'battery')),
('089010', '켐트로닉스', (SELECT id FROM categories WHERE code = 'battery')),
('263750', '펄어비스', (SELECT id FROM categories WHERE code = 'battery')),
('383310', '에코프로에이치엔', (SELECT id FROM categories WHERE code = 'battery')),
('038110', '에코플라스틱', (SELECT id FROM categories WHERE code = 'battery'))
ON CONFLICT (stock_id) DO NOTHING;

-- =====================================================
-- 데이터 확인
-- =====================================================

-- 카테고리 수 확인 (8개여야 함)
SELECT '카테고리 총 개수:', COUNT(*) FROM categories;

-- 종목 수 확인 (160개여야 함)
SELECT '종목 총 개수:', COUNT(*) FROM stocks;

-- 카테고리별 종목 수 확인 (각 20개씩)
SELECT 
    c.name AS 카테고리,
    COUNT(s.id) AS 종목수
FROM categories c
LEFT JOIN stocks s ON c.id = s.category_id
GROUP BY c.id, c.name
ORDER BY c.id;

-- =====================================================
-- 완료!
-- =====================================================

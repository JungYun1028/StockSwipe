-- =====================================================
-- StockSwipe 테이블 분리 마이그레이션 SQL
-- =====================================================
--
-- 기존: stocks (모든 정보가 하나의 테이블)
-- 변경: stock_master (기본 정보) + stock_prices (날짜별 주가)
--
-- 실행 전 백업 필수!
--   pg_dump -d stockswipe -t stocks -t categories > backup_before_migration.sql
--
-- =====================================================

-- =====================================================
-- 1. 기존 stocks 테이블 백업
-- =====================================================
CREATE TABLE IF NOT EXISTS stocks_backup AS SELECT * FROM stocks;

-- =====================================================
-- 2. 새 테이블 생성
-- =====================================================

-- stock_master 테이블
CREATE TABLE IF NOT EXISTS stock_master (
    id BIGSERIAL PRIMARY KEY,
    stock_id VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    description VARCHAR(1000),
    business VARCHAR(1000),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- stock_prices 테이블
CREATE TABLE IF NOT EXISTS stock_prices (
    id BIGSERIAL PRIMARY KEY,
    stock_master_id BIGINT NOT NULL REFERENCES stock_master(id) ON DELETE CASCADE,
    bas_dt VARCHAR(8) NOT NULL,
    isin_cd VARCHAR(20),
    mrkt_ctg VARCHAR(10),
    clpr BIGINT,
    vs BIGINT,
    flt_rt DOUBLE PRECISION,
    mkp BIGINT,
    hipr BIGINT,
    lopr BIGINT,
    trqu BIGINT,
    tr_prc BIGINT,
    lstg_st_cnt BIGINT,
    mrkt_tot_amt BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(stock_master_id, bas_dt)  -- 같은 종목의 같은 날짜는 1개만!
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_stock_master_stock_id ON stock_master(stock_id);
CREATE INDEX IF NOT EXISTS idx_stock_master_category ON stock_master(category_id);
CREATE INDEX IF NOT EXISTS idx_stock_prices_stock_master ON stock_prices(stock_master_id);
CREATE INDEX IF NOT EXISTS idx_stock_prices_bas_dt ON stock_prices(bas_dt);
CREATE INDEX IF NOT EXISTS idx_stock_prices_stock_bas_dt ON stock_prices(stock_master_id, bas_dt);

-- =====================================================
-- 3. 기존 데이터 마이그레이션
-- =====================================================

-- 3-1. stock_master로 기본 정보 이동
INSERT INTO stock_master (stock_id, name, category_id, description, business)
SELECT 
    stock_id,
    name,
    category_id,
    description,
    business
FROM stocks
ON CONFLICT (stock_id) DO NOTHING;

-- 3-2. stock_prices로 주가 정보 이동 (basDt가 있는 것만)
INSERT INTO stock_prices (
    stock_master_id, bas_dt, isin_cd, mrkt_ctg,
    clpr, vs, flt_rt, mkp, hipr, lopr,
    trqu, tr_prc, lstg_st_cnt, mrkt_tot_amt
)
SELECT 
    sm.id,
    s.bas_dt,
    s.isin_cd,
    s.mrkt_ctg,
    s.clpr,
    s.vs,
    s.flt_rt,
    s.mkp,
    s.hipr,
    s.lopr,
    s.trqu,
    s.tr_prc,
    s.lstg_st_cnt,
    s.mrkt_tot_amt
FROM stocks s
JOIN stock_master sm ON s.stock_id = sm.stock_id
WHERE s.bas_dt IS NOT NULL  -- 주가 정보가 있는 것만
ON CONFLICT (stock_master_id, bas_dt) DO NOTHING;

-- =====================================================
-- 4. chart_data, news, stock_keywords 테이블 FK 업데이트
-- =====================================================

-- chart_data 테이블 (stock_id → stock_master_id)
ALTER TABLE chart_data 
    DROP CONSTRAINT IF EXISTS chart_data_stock_id_fkey,
    ADD CONSTRAINT chart_data_stock_master_id_fkey 
    FOREIGN KEY (stock_id) REFERENCES stock_master(id) ON DELETE CASCADE;

ALTER TABLE chart_data RENAME COLUMN stock_id TO stock_master_id;

-- news 테이블 (stock_id → stock_master_id)
ALTER TABLE news
    DROP CONSTRAINT IF EXISTS news_stock_id_fkey,
    ADD CONSTRAINT news_stock_master_id_fkey
    FOREIGN KEY (stock_id) REFERENCES stock_master(id) ON DELETE CASCADE;

ALTER TABLE news RENAME COLUMN stock_id TO stock_master_id;

-- stock_keywords 테이블 (stock_id → stock_master_id)
ALTER TABLE stock_keywords
    DROP CONSTRAINT IF EXISTS stock_keywords_stock_id_fkey,
    ADD CONSTRAINT stock_keywords_stock_master_id_fkey
    FOREIGN KEY (stock_id) REFERENCES stock_master(id) ON DELETE CASCADE;

ALTER TABLE stock_keywords RENAME COLUMN stock_id TO stock_master_id;

-- =====================================================
-- 5. 기존 stocks 테이블 제거 (백업 확인 후)
-- =====================================================

-- 백업 확인 후 주석 해제하여 실행
-- DROP TABLE IF EXISTS stocks CASCADE;

-- =====================================================
-- 6. 마이그레이션 결과 확인
-- =====================================================

SELECT '=== 마이그레이션 결과 ===' AS status;

-- Categories (8개)
SELECT 'Categories:', COUNT(*) FROM categories;

-- StockMaster (160개)
SELECT 'StockMaster:', COUNT(*) FROM stock_master;

-- StockPrices (주가 데이터가 있는 만큼)
SELECT 'StockPrices:', COUNT(*) FROM stock_prices;

-- 카테고리별 종목 수
SELECT 
    c.name AS 카테고리,
    COUNT(sm.id) AS 종목수
FROM categories c
LEFT JOIN stock_master sm ON c.id = sm.category_id
GROUP BY c.id, c.name
ORDER BY c.id;

-- 주가 데이터가 있는 종목 수
SELECT 
    '주가 데이터 있는 종목:',
    COUNT(DISTINCT stock_master_id)
FROM stock_prices;

-- =====================================================
-- 완료!
-- =====================================================

SELECT '✅ 마이그레이션 완료!' AS status;
SELECT '📊 stock_master: 종목 기본 정보 (변하지 않음)' AS info;
SELECT '📈 stock_prices: 날짜별 주가 정보 (INSERT or UPDATE)' AS info;
SELECT '🔑 UNIQUE(stock_master_id, bas_dt): 같은 종목 같은 날짜는 1개만' AS info;

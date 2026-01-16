-- =====================================================
-- StockSwipe 테이블 정리 스크립트
-- =====================================================
--
-- 사용하지 않는 테이블 삭제:
--   - stocks (이전 테이블, stock_master + stock_prices로 분리됨)
--   - stocks_backup (마이그레이션 백업 테이블)
--
-- 실행 시점:
--   migration-to-split-tables.sql 실행 후
--   또는 테이블 분리 마이그레이션 완료 후
--
-- =====================================================

-- 1. stocks 테이블을 참조하는 외래키 제약조건 삭제
ALTER TABLE chart_data DROP CONSTRAINT IF EXISTS fkiulb24g430ogm4tnrufyw5x85;
ALTER TABLE news DROP CONSTRAINT IF EXISTS fk3x7k8lf27cib895pmsy8q2nnv;
ALTER TABLE stock_keywords DROP CONSTRAINT IF EXISTS fkjyjfl3wxr6kfc835dpqkn72pg;

-- 2. stocks 테이블 삭제
DROP TABLE IF EXISTS stocks CASCADE;

-- 3. stocks_backup 테이블 삭제
DROP TABLE IF EXISTS stocks_backup CASCADE;

-- 4. 삭제 확인
SELECT '✅ stocks 테이블 삭제 완료' AS status;
SELECT '✅ stocks_backup 테이블 삭제 완료' AS status;

-- 5. 남은 stock 관련 테이블 확인
SELECT 
    table_name,
    (SELECT COUNT(*) FROM information_schema.columns 
     WHERE table_name = t.table_name) as column_count
FROM information_schema.tables t
WHERE table_schema = 'public' 
  AND table_name LIKE '%stock%'
ORDER BY table_name;

-- =====================================================
-- 예상 결과:
--   stock_keywords (3 columns)
--   stock_master (8 columns)
--   stock_prices (17 columns)
-- =====================================================

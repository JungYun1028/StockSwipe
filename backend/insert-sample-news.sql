-- 샘플 뉴스 데이터 삽입 (화면 테스트용)
-- 실행: psql -d stockswipe -f backend/insert-sample-news.sql

-- 삼양식품 (003230) 뉴스
INSERT INTO news (news_id, title, summary, link, source, stock_master_id)
SELECT 
    gen_random_uuid()::text,
    '삼양식품, 불닭볶음면 매출 급증',
    '삼양식품의 인기 제품 불닭볶음면이 해외에서 큰 인기를 끌고 있다.',
    'https://finance.naver.com/item/news.naver',
    '한국경제',
    sm.id
FROM stock_master sm WHERE sm.stock_id = '003230';

INSERT INTO news (news_id, title, summary, link, source, stock_master_id)
SELECT 
    gen_random_uuid()::text,
    '삼양식품, 신제품 출시 예고',
    '삼양식품이 새로운 라면 제품 라인업을 준비 중이다.',
    'https://finance.naver.com/item/news.naver',
    '매일경제',
    sm.id
FROM stock_master sm WHERE sm.stock_id = '003230';

INSERT INTO news (news_id, title, summary, link, source, stock_master_id)
SELECT 
    gen_random_uuid()::text,
    '삼양식품 주가, 실적 호조에 상승세',
    '삼양식품의 주가가 실적 개선 기대감에 상승하고 있다.',
    'https://finance.naver.com/item/news.naver',
    '연합뉴스',
    sm.id
FROM stock_master sm WHERE sm.stock_id = '003230';

-- 삼성전자 (005930) 뉴스
INSERT INTO news (news_id, title, summary, link, source, stock_master_id)
SELECT 
    gen_random_uuid()::text,
    '삼성전자, 반도체 시장 회복 기대',
    '삼성전자가 메모리 반도체 시장 회복에 따라 실적 개선이 예상된다.',
    'https://finance.naver.com/item/news.naver',
    '서울경제',
    sm.id
FROM stock_master sm WHERE sm.stock_id = '005930';

INSERT INTO news (news_id, title, summary, link, source, stock_master_id)
SELECT 
    gen_random_uuid()::text,
    '삼성전자, AI 칩 개발 박차',
    '삼성전자가 인공지능(AI) 반도체 개발에 속도를 내고 있다.',
    'https://finance.naver.com/item/news.naver',
    '조선일보',
    sm.id
FROM stock_master sm WHERE sm.stock_id = '005930';

SELECT '✅ 샘플 뉴스 데이터 삽입 완료' AS status;
SELECT COUNT(*) AS news_count FROM news;

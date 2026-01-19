package com.stockswipe.service;

import com.stockswipe.model.News;
import com.stockswipe.model.StockMaster;
import com.stockswipe.repository.NewsRepository;
import com.stockswipe.repository.StockMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 구글 뉴스 크롤링 서비스
 * - 종목명으로 구글 뉴스 RSS 검색
 * - 뉴스 제목, 링크, 출처 추출
 * - DB에 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleNewsCrawler {
    
    // 구글 뉴스 RSS URL
    private static final String GOOGLE_NEWS_RSS = "https://news.google.com/rss/search?q=";
    private static final String RSS_PARAMS = "&hl=ko&gl=KR&ceid=KR:ko";
    private static final int MAX_RETRIES = 3;
    
    private final StockMasterRepository stockMasterRepository;
    private final NewsRepository newsRepository;
    private final OpenAiService openAiService;
    
    /**
     * 특정 종목의 뉴스를 크롤링하여 DB에 저장
     * 
     * @param stockId 종목코드 (예: "005930")
     * @param count 가져올 뉴스 개수 (기본 10개)
     * @return 저장된 뉴스 개수
     */
    @Transactional
    public int crawlAndSaveNews(String stockId, int count) {
        StockMaster stockMaster = stockMasterRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("StockMaster not found: " + stockId));
        
        // 종목명으로 구글 뉴스 크롤링
        String keyword = stockMaster.getName() + " 주식"; // 검색어에 "주식" 추가
        List<NewsItem> newsItems = crawlGoogleNews(keyword, count);
        
        // 기존 뉴스 삭제 (선택사항 - 필요시 주석 처리)
        // newsRepository.deleteByStockMaster(stockMaster);
        
        int savedCount = 0;
        for (NewsItem item : newsItems) {
            // 중복 체크 (링크 기준)
            boolean exists = newsRepository.findAll().stream()
                    .anyMatch(n -> n.getLink() != null && n.getLink().equals(item.link));
            
            if (!exists) {
                // OpenAI로 뉴스 감성 분석 (호재/악재 판단)
                java.util.Map<String, Object> sentimentResult = openAiService.analyzeNewsSentiment(
                        stockMaster.getName(), 
                        item.title, 
                        ""
                );
                String sentiment = (String) sentimentResult.get("sentiment");
                Double sentimentScore = (Double) sentimentResult.get("score");
                
                log.info("📊 뉴스 감성 분석 - {}: {} ({})", 
                        item.title.substring(0, Math.min(30, item.title.length())), 
                        sentiment, 
                        sentimentScore);
                
                News news = new News(
                        UUID.randomUUID().toString(), // newsId
                        item.title,
                        "", // summary는 나중에 추가 가능
                        item.link,
                        item.source,
                        sentiment,
                        sentimentScore
                );
                news.setStockMaster(stockMaster);
                newsRepository.save(news);
                savedCount++;
                
                // OpenAI API 호출 제한 방지 (초당 3건)
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        log.info("✅ {} 뉴스 크롤링 완료: {}개 저장", stockMaster.getName(), savedCount);
        
        // 뉴스 크롤링 완료 후 AI 전문가 분석 생성
        if (savedCount > 0) {
            try {
                openAiService.generateAnalystRating(stockId);
            } catch (Exception e) {
                log.error("❌ {} AI 전문가 분석 생성 실패: {}", stockMaster.getName(), e.getMessage());
            }
        }
        
        return savedCount;
    }
    
    /**
     * 모든 종목의 뉴스를 크롤링
     * 
     * @param count 종목당 가져올 뉴스 개수
     */
    @Transactional
    public void crawlAllStocksNews(int count) {
        List<StockMaster> stockMasters = stockMasterRepository.findAll();
        log.info("📰 총 {}개 종목의 뉴스를 크롤링합니다...", stockMasters.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < stockMasters.size(); i++) {
            StockMaster stockMaster = stockMasters.get(i);
            try {
                int saved = crawlAndSaveNews(stockMaster.getStockId(), count);
                successCount++;
                log.info("✅ [{}/{}] {} 뉴스 크롤링 완료 ({}개 저장)", 
                        i + 1, stockMasters.size(), stockMaster.getName(), saved);
                
                // API 호출 제한 방지 (초당 2건)
                Thread.sleep(500);
                
            } catch (Exception e) {
                failCount++;
                log.error("❌ [{}/{}] {} 뉴스 크롤링 실패: {}", 
                        i + 1, stockMasters.size(), stockMaster.getName(), e.getMessage());
            }
        }
        
        log.info("🎉 뉴스 크롤링 완료! 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * 구글 뉴스 RSS 크롤링
     * 
     * @param keyword 검색 키워드 (종목명)
     * @param count 가져올 뉴스 개수
     * @return 뉴스 아이템 리스트
     */
    public List<NewsItem> crawlGoogleNews(String keyword, int count) {
        List<NewsItem> results = new ArrayList<>();
        
        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                String url = GOOGLE_NEWS_RSS + encodedKeyword + RSS_PARAMS;
                log.debug("구글 뉴스 RSS 크롤링 URL: {}", url);
                
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .timeout(15000)
                        .ignoreContentType(true) // RSS는 XML이므로 필수
                        .get();
                
                // RSS 피드 파싱: <item><title>, <link>, <source>
                Elements items = doc.select("item");
                
                for (Element item : items) {
                    Element titleElem = item.selectFirst("title");
                    Element linkElem = item.selectFirst("link");
                    Element sourceElem = item.selectFirst("source");
                    
                    if (titleElem != null && linkElem != null) {
                        String title = titleElem.text().trim();
                        String link = linkElem.text().trim();
                        String source = sourceElem != null ? sourceElem.text().trim() : "구글뉴스";
                        
                        if (!title.isEmpty() && !link.isEmpty()) {
                            results.add(new NewsItem(title, link, source));
                            if (results.size() >= count) break;
                        }
                    }
                }
                
                if (!results.isEmpty()) {
                    log.info("✅ 구글 뉴스 {}개 크롤링 성공 (키워드: {})", results.size(), keyword);
                    break; // 성공하면 재시도 중단
                } else {
                    log.warn("구글 뉴스 크롤링 결과 없음 (키워드: {}, 시도: {}/{})", 
                            keyword, retry + 1, MAX_RETRIES);
                }
                
            } catch (IOException e) {
                log.warn("구글 뉴스 크롤링 실패 (키워드: {}, 시도: {}/{}): {}", 
                        keyword, retry + 1, MAX_RETRIES, e.getMessage());
                if (retry < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(1000); // 1초 대기 후 재시도
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.error("뉴스 크롤링 중 예상치 못한 오류 (키워드: {}): {}", keyword, e.getMessage());
                break;
            }
        }
        
        return results;
    }
    
    /**
     * 뉴스 아이템 내부 클래스
     */
    public static class NewsItem {
        String title;
        String link;
        String source;
        
        NewsItem(String title, String link, String source) {
            this.title = title;
            this.link = link;
            this.source = source;
        }
        
        @Override
        public String toString() {
            return "제목: " + title + "\n링크: " + link + "\n출처: " + source + "\n";
        }
    }
}

package com.stockswipe.service;

import com.stockswipe.model.News;
import com.stockswipe.model.Stock;
import com.stockswipe.repository.NewsRepository;
import com.stockswipe.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    // 구글 뉴스 RSS URL
    private static final String GOOGLE_NEWS_RSS = "https://news.google.com/rss/search?q=";
    private static final String RSS_PARAMS = "&hl=ko&gl=KR&ceid=KR:ko";
    private static final int MAX_RETRIES = 3;
    private static final int MAX_NEWS_PER_STOCK = 5; // 종목당 최대 뉴스 개수

    private final NewsRepository newsRepository;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    /**
     * 특정 종목의 뉴스를 가져와서 저장
     */
    @Transactional
    public int fetchAndSaveNewsForStock(String stockId) {
        Stock stock = stockRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockId));

        try {
            // 종목명으로 검색
            String searchQuery = stock.getName() + " 주식";
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String rssUrl = GOOGLE_NEWS_RSS + encodedQuery + RSS_PARAMS;

            log.info("📰 {} 종목 뉴스 가져오기: {}", stock.getName(), rssUrl);

            // RSS 피드 가져오기
            String rssContent = fetchRssWithRetry(rssUrl);
            if (rssContent == null || rssContent.isEmpty()) {
                log.warn("⚠️ {} 종목 뉴스 RSS를 가져올 수 없습니다.", stock.getName());
                return 0;
            }

            // RSS 파싱
            List<NewsItem> newsItems = parseRss(rssContent);
            log.info("📰 파싱된 뉴스: {}개", newsItems.size());

            // 뉴스 저장
            int savedCount = 0;
            for (NewsItem item : newsItems) {
                if (savedCount >= MAX_NEWS_PER_STOCK) {
                    break;
                }

                // 중복 체크
                if (newsRepository.findByNewsIdAndStock(item.getId(), stock).isPresent()) {
                    continue;
                }

                News news = new News();
                news.setNewsId(item.getId());
                news.setTitle(item.getTitle());
                news.setSummary(item.getDescription());
                news.setStock(stock);

                newsRepository.save(news);
                savedCount++;
            }

            log.info("✅ {} 종목 뉴스 저장 완료: {}개", stock.getName(), savedCount);
            return savedCount;

        } catch (Exception e) {
            log.error("❌ {} 종목 뉴스 가져오기 실패: {}", stock.getName(), e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 모든 종목의 뉴스를 가져와서 저장
     */
    @Transactional
    public Map<String, Integer> fetchAndSaveAllNews() {
        List<Stock> stocks = stockRepository.findAll();
        log.info("📰 총 {}개 종목의 뉴스를 가져옵니다...", stocks.size());

        Map<String, Integer> result = new HashMap<>();
        int totalFetched = 0;
        int totalStocksProcessed = 0;

        for (Stock stock : stocks) {
            try {
                int fetched = fetchAndSaveNewsForStock(stock.getStockId());
                result.put(stock.getStockId(), fetched);
                totalFetched += fetched;
                totalStocksProcessed++;

                // API 호출 제한 방지
                Thread.sleep(500); // 0.5초 대기

            } catch (Exception e) {
                log.error("❌ {} 종목 뉴스 가져오기 실패: {}", stock.getName(), e.getMessage());
            }
        }

        result.put("totalStocksProcessed", totalStocksProcessed);
        result.put("totalNewsFetched", totalFetched);

        log.info("🎉 모든 종목 뉴스 가져오기 완료! 처리: {}개, 총 뉴스: {}개", totalStocksProcessed, totalFetched);
        return result;
    }

    /**
     * 재시도 로직이 포함된 RSS 가져오기
     */
    private String fetchRssWithRetry(String url) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                URI uri = URI.create(url);
                String response = restTemplate.getForObject(uri, String.class);
                if (response != null && !response.isEmpty()) {
                    return response;
                }
            } catch (Exception e) {
                log.warn("⚠️ RSS 가져오기 실패 (시도 {}/{}): {}", i + 1, MAX_RETRIES, e.getMessage());
                if (i < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(1000 * (i + 1)); // 지수 백오프
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return null;
    }

    /**
     * RSS XML 파싱
     */
    private List<NewsItem> parseRss(String rssContent) {
        List<NewsItem> newsItems = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(rssContent.getBytes(StandardCharsets.UTF_8)));

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String title = getElementText(item, "title");
                String description = getElementText(item, "description");
                String link = getElementText(item, "link");
                String pubDate = getElementText(item, "pubDate");

                if (title != null && !title.isEmpty()) {
                    // 뉴스 ID 생성 (링크에서 추출하거나 제목 기반)
                    String newsId = generateNewsId(link, title);

                    NewsItem newsItem = new NewsItem();
                    newsItem.setId(newsId);
                    newsItem.setTitle(cleanText(title));
                    newsItem.setDescription(cleanText(description != null ? description : title));
                    newsItem.setLink(link);
                    newsItem.setPubDate(pubDate);

                    newsItems.add(newsItem);
                }
            }

        } catch (Exception e) {
            log.error("❌ RSS 파싱 실패: {}", e.getMessage(), e);
        }

        return newsItems;
    }

    /**
     * 뉴스 ID 생성 (링크 기반 또는 제목 기반)
     */
    private String generateNewsId(String link, String title) {
        if (link != null && !link.isEmpty()) {
            // 링크에서 ID 추출 시도
            try {
                // Google News 링크 형식: https://news.google.com/rss/articles/...
                if (link.contains("/articles/")) {
                    String[] parts = link.split("/articles/");
                    if (parts.length > 1) {
                        return parts[1].substring(0, Math.min(50, parts[1].length()));
                    }
                }
                // 일반 링크의 경우 해시 사용
                return String.valueOf(link.hashCode());
            } catch (Exception e) {
                // 링크 파싱 실패 시 제목 기반
            }
        }
        // 제목 기반 해시
        return String.valueOf(title.hashCode());
    }

    /**
     * HTML 태그 제거 및 텍스트 정리
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        // HTML 태그 제거
        text = text.replaceAll("<[^>]+>", "");
        // 엔티티 디코딩
        text = text.replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&amp;", "&")
                   .replace("&quot;", "\"")
                   .replace("&#39;", "'");
        // 공백 정리
        text = text.replaceAll("\\s+", " ").trim();
        // 최대 길이 제한
        if (text.length() > 1000) {
            text = text.substring(0, 1000) + "...";
        }
        return text;
    }

    /**
     * XML 요소에서 텍스트 추출
     */
    private String getElementText(Element parent, String tagName) {
        try {
            NodeList nodeList = parent.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        } catch (Exception e) {
            // 무시
        }
        return null;
    }

    /**
     * 뉴스 아이템 임시 클래스
     */
    private static class NewsItem {
        private String id;
        private String title;
        private String description;
        private String link;
        private String pubDate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPubDate() {
            return pubDate;
        }

        public void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }
    }
}

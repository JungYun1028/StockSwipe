package com.stockswipe.service;

import com.stockswipe.model.Stock;
import com.stockswipe.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockApiService {
    
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${stock.api.key}")
    private String apiKey;
    
    @Value("${stock.api.base-url}")
    private String baseUrl;
    
    /**
     * 모든 종목의 데이터를 API로부터 가져와서 업데이트
     */
    public void updateAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        log.info("📊 총 {}개 종목의 데이터를 가져옵니다...", stocks.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            try {
                // stockId로 업데이트 (각 업데이트마다 별도 트랜잭션)
                boolean success = updateStockDataByStockId(stock.getStockId());
                if (success) {
                    successCount++;
                    log.info("✅ [{}/{}] {} ({}) 업데이트 완료", 
                            i + 1, stocks.size(), stock.getName(), stock.getStockId());
                } else {
                    failCount++;
                    log.warn("⚠️ [{}/{}] {} ({}) 데이터 없음", 
                            i + 1, stocks.size(), stock.getName(), stock.getStockId());
                }
                
                // API 호출 제한 방지 (초당 10건)
                Thread.sleep(100);
                
            } catch (Exception e) {
                failCount++;
                log.error("❌ [{}/{}] {} ({}) 업데이트 실패: {}", 
                        i + 1, stocks.size(), stock.getName(), stock.getStockId(), e.getMessage());
            }
        }
        
        log.info("🎉 업데이트 완료! 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * stockId로 종목을 조회하여 업데이트 (트랜잭션 보장)
     */
    @Transactional
    public boolean updateStockDataByStockId(String stockId) {
        Stock stock = stockRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockId));
        return updateStockData(stock);
    }
    
    /**
     * 개별 종목 데이터 업데이트
     */
    @Transactional
    public boolean updateStockData(Stock stock) {
        try {
            // 어제 날짜 계산 (SYSDATE-1)
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String basDt = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // URL을 직접 구성하여 인코딩 문제 방지
            String url = baseUrl + "/getStockPriceInfo?serviceKey=" + apiKey + 
                        "&numOfRows=1&pageNo=1&likeSrtnCd=" + stock.getStockId() +
                        "&basDt=" + basDt;
            
            log.debug("API 요청 URL ({}): basDt={}", stock.getStockId(), basDt);
            
            // URI 객체로 변환 (재인코딩 방지)
            URI uri = URI.create(url);
            
            String response = restTemplate.getForObject(uri, String.class);
            
            if (response == null || response.isEmpty()) {
                return false;
            }
            
            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
            
            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) {
                return false;
            }
            
            Element item = (Element) items.item(0);
            
            // 데이터 업데이트
            stock.setBasDt(getElementText(item, "basDt"));
            stock.setIsinCd(getElementText(item, "isinCd"));
            stock.setMrktCtg(getElementText(item, "mrktCtg"));
            stock.setClpr(getElementLong(item, "clpr"));
            stock.setVs(getElementLong(item, "vs"));
            stock.setFltRt(getElementDouble(item, "fltRt"));
            stock.setMkp(getElementLong(item, "mkp"));
            stock.setHipr(getElementLong(item, "hipr"));
            stock.setLopr(getElementLong(item, "lopr"));
            stock.setTrqu(getElementLong(item, "trqu"));
            stock.setTrPrc(getElementLong(item, "trPrc"));
            stock.setLstgStCnt(getElementLong(item, "lstgStCnt"));
            stock.setMrktTotAmt(getElementLong(item, "mrktTotAmt"));
            
            stockRepository.save(stock);
            return true;
            
        } catch (Exception e) {
            log.error("종목 {} 데이터 업데이트 실패: {}", stock.getStockId(), e.getMessage());
            return false;
        }
    }
    
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
    
    private Long getElementLong(Element parent, String tagName) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        return null;
    }
    
    private Double getElementDouble(Element parent, String tagName) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        return null;
    }
}


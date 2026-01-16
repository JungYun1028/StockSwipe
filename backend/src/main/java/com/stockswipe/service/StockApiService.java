package com.stockswipe.service;

import com.stockswipe.model.StockMaster;
import com.stockswipe.model.StockPrice;
import com.stockswipe.repository.StockMasterRepository;
import com.stockswipe.repository.StockPriceRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockApiService {
    
    private final StockMasterRepository stockMasterRepository;
    private final StockPriceRepository stockPriceRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${stock.api.key}")
    private String apiKey;
    
    @Value("${stock.api.base-url}")
    private String baseUrl;
    
    /**
     * 모든 종목의 데이터를 API로부터 가져와서 업데이트
     */
    public void updateAllStocks() {
        List<StockMaster> stockMasters = stockMasterRepository.findAll();
        log.info("📊 총 {}개 종목의 데이터를 가져옵니다...", stockMasters.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < stockMasters.size(); i++) {
            StockMaster stockMaster = stockMasters.get(i);
            try {
                // stockId로 업데이트 (각 업데이트마다 별도 트랜잭션)
                boolean success = updateStockDataByStockId(stockMaster.getStockId());
                if (success) {
                    successCount++;
                    log.info("✅ [{}/{}] {} ({}) 업데이트 완료", 
                            i + 1, stockMasters.size(), stockMaster.getName(), stockMaster.getStockId());
                } else {
                    failCount++;
                    log.warn("⚠️ [{}/{}] {} ({}) 데이터 없음", 
                            i + 1, stockMasters.size(), stockMaster.getName(), stockMaster.getStockId());
                }
                
                // API 호출 제한 방지 (초당 10건)
                Thread.sleep(100);
                
            } catch (Exception e) {
                failCount++;
                log.error("❌ [{}/{}] {} ({}) 업데이트 실패: {}", 
                        i + 1, stockMasters.size(), stockMaster.getName(), stockMaster.getStockId(), e.getMessage());
            }
        }
        
        log.info("🎉 업데이트 완료! 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * stockId로 종목을 조회하여 업데이트 (트랜잭션 보장)
     */
    @Transactional
    public boolean updateStockDataByStockId(String stockId) {
        StockMaster stockMaster = stockMasterRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("StockMaster not found: " + stockId));
        return updateStockData(stockMaster);
    }
    
    /**
     * 개별 종목 데이터 업데이트 (INSERT or UPDATE)
     * - 같은 종목의 같은 날짜: UPDATE
     * - 같은 종목의 다른 날짜: INSERT
     */
    @Transactional
    public boolean updateStockData(StockMaster stockMaster) {
        try {
            // 어제 날짜 계산 (SYSDATE-1)
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String basDt = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // URL을 직접 구성하여 인코딩 문제 방지
            String url = baseUrl + "/getStockPriceInfo?serviceKey=" + apiKey + 
                        "&numOfRows=1&pageNo=1&likeSrtnCd=" + stockMaster.getStockId() +
                        "&basDt=" + basDt;
            
            log.debug("API 요청 URL ({}): basDt={}", stockMaster.getStockId(), basDt);
            
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
            
            // 같은 종목의 같은 날짜 데이터가 있는지 확인
            Optional<StockPrice> existingPrice = stockPriceRepository
                    .findByStockMasterAndBasDt(stockMaster, basDt);
            
            StockPrice stockPrice;
            if (existingPrice.isPresent()) {
                // UPDATE: 이미 존재하면 업데이트
                stockPrice = existingPrice.get();
                log.debug("UPDATE: {} ({}) 기존 데이터 업데이트", stockMaster.getName(), basDt);
            } else {
                // INSERT: 새로운 날짜 데이터 생성
                stockPrice = new StockPrice(stockMaster, basDt);
                log.debug("INSERT: {} ({}) 새 데이터 생성", stockMaster.getName(), basDt);
            }
            
            // 데이터 설정
            stockPrice.setIsinCd(getElementText(item, "isinCd"));
            stockPrice.setMrktCtg(getElementText(item, "mrktCtg"));
            stockPrice.setClpr(getElementLong(item, "clpr"));
            stockPrice.setVs(getElementLong(item, "vs"));
            stockPrice.setFltRt(getElementDouble(item, "fltRt"));
            stockPrice.setMkp(getElementLong(item, "mkp"));
            stockPrice.setHipr(getElementLong(item, "hipr"));
            stockPrice.setLopr(getElementLong(item, "lopr"));
            stockPrice.setTrqu(getElementLong(item, "trqu"));
            stockPrice.setTrPrc(getElementLong(item, "trPrc"));
            stockPrice.setLstgStCnt(getElementLong(item, "lstgStCnt"));
            stockPrice.setMrktTotAmt(getElementLong(item, "mrktTotAmt"));
            
            stockPriceRepository.save(stockPrice);
            return true;
            
        } catch (Exception e) {
            log.error("종목 {} 데이터 업데이트 실패: {}", stockMaster.getStockId(), e.getMessage());
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


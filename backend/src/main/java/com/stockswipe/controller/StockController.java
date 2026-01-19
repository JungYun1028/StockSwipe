package com.stockswipe.controller;

import com.stockswipe.dto.CategoryDTO;
import com.stockswipe.dto.StockDTO;
import com.stockswipe.service.NewsService;
import com.stockswipe.service.OpenAiService;
import com.stockswipe.service.StockApiService;
import com.stockswipe.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {
    
    private final StockService stockService;
    private final StockApiService stockApiService;
    private final OpenAiService openAiService;
    private final NewsService newsService;
    
    @GetMapping("/stocks")
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }
    
    @GetMapping("/stocks/{stockId}")
    public ResponseEntity<StockDTO> getStockById(@PathVariable String stockId) {
        return ResponseEntity.ok(stockService.getStockById(stockId));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(stockService.getCategories());
    }
    
    @GetMapping("/stocks/category/{categoryCode}")
    public ResponseEntity<List<StockDTO>> getStocksByCategory(@PathVariable String categoryCode) {
        return ResponseEntity.ok(stockService.getStocksByCategory(categoryCode));
    }
    
    @PostMapping("/stocks/update-from-api")
    public ResponseEntity<Map<String, String>> updateStocksFromApi() {
        // 동기적으로 실행 (트랜잭션 보장)
        stockApiService.updateAllStocks();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "completed");
        response.put("message", "주식 데이터 업데이트가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/stocks/update-one/{stockId}")
    public ResponseEntity<Map<String, Object>> updateOneStock(@PathVariable String stockId) {
        boolean success = stockApiService.updateStockDataByStockId(stockId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("stockId", stockId);
        response.put("message", success ? "업데이트 완료" : "업데이트 실패");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/stocks/generate-ai-info")
    public ResponseEntity<Map<String, String>> generateAllStocksAiInfo() {
        openAiService.generateAllStocksInfo();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "completed");
        response.put("message", "OpenAI 정보 생성이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/stocks/generate-ai-info/{stockId}")
    public ResponseEntity<Map<String, String>> generateOneStockAiInfo(@PathVariable String stockId) {
        openAiService.generateStockInfo(stockId);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "completed");
        response.put("stockId", stockId);
        response.put("message", "OpenAI 정보 생성이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 종목의 뉴스 가져오기
     */
    @PostMapping("/stocks/{stockId}/fetch-news")
    public ResponseEntity<Map<String, Object>> fetchNewsForStock(@PathVariable String stockId) {
        int fetchedCount = newsService.fetchAndSaveNewsForStock(stockId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("stockId", stockId);
        response.put("fetchedNewsCount", fetchedCount);
        response.put("message", fetchedCount > 0 ? fetchedCount + "개의 뉴스를 가져왔습니다." : "새로운 뉴스가 없습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 모든 종목의 뉴스 가져오기
     */
    @PostMapping("/stocks/fetch-all-news")
    public ResponseEntity<Map<String, Object>> fetchAllNews() {
        Map<String, Integer> result = newsService.fetchAndSaveAllNews();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("totalStocksProcessed", result.get("totalStocksProcessed"));
        response.put("totalNewsFetched", result.get("totalNewsFetched"));
        response.put("message", "모든 종목의 뉴스 가져오기 작업이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 포트폴리오 분석 및 AI 조언
     */
    @PostMapping("/portfolio/analyze")
    public ResponseEntity<Map<String, Object>> analyzePortfolio(@RequestBody Map<String, Object> portfolioData) {
        try {
            // null 체크 및 안전한 타입 변환
            @SuppressWarnings("unchecked")
            List<String> stockNames = portfolioData.get("stockNames") != null 
                ? (List<String>) portfolioData.get("stockNames") 
                : new java.util.ArrayList<>();
            
            @SuppressWarnings("unchecked")
            List<String> categories = portfolioData.get("categories") != null 
                ? (List<String>) portfolioData.get("categories") 
                : new java.util.ArrayList<>();
            
            Double avgReturn = portfolioData.get("avgReturn") != null 
                ? ((Number) portfolioData.get("avgReturn")).doubleValue() 
                : 0.0;
            
            Integer upStocks = portfolioData.get("upStocks") != null 
                ? ((Number) portfolioData.get("upStocks")).intValue() 
                : 0;
            
            Integer downStocks = portfolioData.get("downStocks") != null 
                ? ((Number) portfolioData.get("downStocks")).intValue() 
                : 0;
            
            @SuppressWarnings("unchecked")
            Map<String, Integer> sectorDistribution = portfolioData.get("sectorDistribution") != null 
                ? (Map<String, Integer>) portfolioData.get("sectorDistribution") 
                : new java.util.HashMap<>();

            String advice = openAiService.analyzePortfolio(
                stockNames, categories, avgReturn, upStocks, downStocks, sectorDistribution
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "completed");
            response.put("advice", advice);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("advice", "포트폴리오 분석 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}


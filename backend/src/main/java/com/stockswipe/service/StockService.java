package com.stockswipe.service;

import com.stockswipe.dto.*;
import com.stockswipe.model.*;
import com.stockswipe.repository.CategoryRepository;
import com.stockswipe.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockRepository stockRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<StockDTO> getAllStocks() {
        return stockRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public StockDTO getStockById(String stockId) {
        Stock stock = stockRepository.findByStockIdWithDetails(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockId));
        return convertToDTO(stock);
    }
    
    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByCategory(String categoryCode) {
        return stockRepository.findAllWithDetails().stream()
                .filter(stock -> stock.getCategory() != null && 
                        stock.getCategory().getCode().equals(categoryCode))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertCategoryToDTO)
                .collect(Collectors.toList());
    }
    
    private CategoryDTO convertCategoryToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getCode(),
                category.getName()
        );
    }
    
    private StockDTO convertToDTO(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setId(stock.getStockId());
        dto.setName(stock.getName());
        
        // Category
        if (stock.getCategory() != null) {
            dto.setCategory(convertCategoryToDTO(stock.getCategory()));
        }
        
        // 공공 API 데이터
        dto.setBasDt(stock.getBasDt());
        dto.setIsinCd(stock.getIsinCd());
        dto.setMrktCtg(stock.getMrktCtg());
        dto.setClpr(stock.getClpr());
        dto.setVs(stock.getVs());
        dto.setFltRt(stock.getFltRt());
        dto.setMkp(stock.getMkp());
        dto.setHipr(stock.getHipr());
        dto.setLopr(stock.getLopr());
        dto.setTrqu(stock.getTrqu());
        dto.setTrPrc(stock.getTrPrc());
        dto.setLstgStCnt(stock.getLstgStCnt());
        dto.setMrktTotAmt(stock.getMrktTotAmt());
        
        // OpenAI 생성 데이터
        dto.setDescription(stock.getDescription());
        dto.setBusiness(stock.getBusiness());
        
        // 관계 데이터
        dto.setKeywords(stock.getKeywords());
        
        // Chart Data
        List<ChartDataDTO> chartDataDTOs = stock.getChartData().stream()
                .map(cd -> new ChartDataDTO(cd.getTime(), cd.getPrice()))
                .collect(Collectors.toList());
        dto.setChartData(chartDataDTOs);
        
        // News
        List<NewsDTO> newsDTOs = stock.getNews().stream()
                .map(n -> new NewsDTO(n.getNewsId(), n.getTitle(), n.getSummary()))
                .collect(Collectors.toList());
        dto.setNews(newsDTOs);
        
        return dto;
    }
}


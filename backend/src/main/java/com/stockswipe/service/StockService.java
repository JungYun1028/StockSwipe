package com.stockswipe.service;

import com.stockswipe.dto.*;
import com.stockswipe.model.*;
import com.stockswipe.repository.CategoryRepository;
import com.stockswipe.repository.StockMasterRepository;
import com.stockswipe.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMasterRepository stockMasterRepository;
    private final StockPriceRepository stockPriceRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<StockDTO> getAllStocks() {
        List<StockMaster> stockMasters = stockMasterRepository.findAll();
        return stockMasters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public StockDTO getStockById(String stockId) {
        StockMaster stockMaster = stockMasterRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockId));
        return convertToDTO(stockMaster);
    }
    
    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByCategory(String categoryCode) {
        return stockMasterRepository.findAll().stream()
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
    
    private StockDTO convertToDTO(StockMaster stockMaster) {
        StockDTO dto = new StockDTO();
        dto.setId(stockMaster.getStockId());
        dto.setName(stockMaster.getName());
        
        // Category
        if (stockMaster.getCategory() != null) {
            dto.setCategory(convertCategoryToDTO(stockMaster.getCategory()));
        }
        
        // 최신 주가 정보 가져오기
        Optional<StockPrice> latestPrice = stockPriceRepository.findLatestByStockMaster(stockMaster);
        if (latestPrice.isPresent()) {
            StockPrice price = latestPrice.get();
            dto.setBasDt(price.getBasDt());
            dto.setIsinCd(price.getIsinCd());
            dto.setMrktCtg(price.getMrktCtg());
            dto.setClpr(price.getClpr());
            dto.setVs(price.getVs());
            dto.setFltRt(price.getFltRt());
            dto.setMkp(price.getMkp());
            dto.setHipr(price.getHipr());
            dto.setLopr(price.getLopr());
            dto.setTrqu(price.getTrqu());
            dto.setTrPrc(price.getTrPrc());
            dto.setLstgStCnt(price.getLstgStCnt());
            dto.setMrktTotAmt(price.getMrktTotAmt());
        }
        
        // OpenAI 생성 데이터
        dto.setDescription(stockMaster.getDescription());
        dto.setBusiness(stockMaster.getBusiness());
        
        // 관계 데이터
        dto.setKeywords(stockMaster.getKeywords());
        
        // Chart Data
        List<ChartDataDTO> chartDataDTOs = stockMaster.getChartData().stream()
                .map(cd -> new ChartDataDTO(cd.getTime(), cd.getPrice()))
                .collect(Collectors.toList());
        dto.setChartData(chartDataDTOs);
        
        // News
        List<NewsDTO> newsDTOs = stockMaster.getNews().stream()
                .map(n -> new NewsDTO(
                        n.getNewsId(), 
                        n.getTitle(), 
                        n.getSummary(),
                        n.getLink(),
                        n.getSource()
                ))
                .collect(Collectors.toList());
        dto.setNews(newsDTOs);
        
        return dto;
    }
}


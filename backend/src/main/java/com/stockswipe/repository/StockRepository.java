package com.stockswipe.repository;

import com.stockswipe.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStockId(String stockId);
    
    @Query("SELECT DISTINCT s FROM Stock s LEFT JOIN FETCH s.chartData LEFT JOIN FETCH s.news")
    List<Stock> findAllWithDetails();
    
    @Query("SELECT DISTINCT s FROM Stock s LEFT JOIN FETCH s.chartData LEFT JOIN FETCH s.news WHERE s.stockId = :stockId")
    Optional<Stock> findByStockIdWithDetails(String stockId);
}


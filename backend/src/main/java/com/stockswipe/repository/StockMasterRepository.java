package com.stockswipe.repository;

import com.stockswipe.model.StockMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockMasterRepository extends JpaRepository<StockMaster, Long> {
    Optional<StockMaster> findByStockId(String stockId);
    
    @Query("SELECT DISTINCT s FROM StockMaster s LEFT JOIN FETCH s.news")
    List<StockMaster> findAllWithDetails();
    
    @Query("SELECT DISTINCT s FROM StockMaster s LEFT JOIN FETCH s.news WHERE s.stockId = :stockId")
    Optional<StockMaster> findByStockIdWithDetails(String stockId);
}

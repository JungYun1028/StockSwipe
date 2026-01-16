package com.stockswipe.repository;

import com.stockswipe.model.Category;
import com.stockswipe.model.StockMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockMasterRepository extends JpaRepository<StockMaster, Long> {
    
    Optional<StockMaster> findByStockId(String stockId);
    
    List<StockMaster> findByCategory(Category category);
    
    boolean existsByStockId(String stockId);
}

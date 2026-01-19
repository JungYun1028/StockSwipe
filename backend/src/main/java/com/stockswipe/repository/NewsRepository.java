package com.stockswipe.repository;

import com.stockswipe.model.News;
import com.stockswipe.model.StockMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    List<News> findByStockMaster(StockMaster stockMaster);
    
    List<News> findByStockMasterOrderByIdDesc(StockMaster stockMaster);
    
    void deleteByStockMaster(StockMaster stockMaster);
}

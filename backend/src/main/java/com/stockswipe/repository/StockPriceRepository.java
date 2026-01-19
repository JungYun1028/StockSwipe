package com.stockswipe.repository;

import com.stockswipe.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockMaster_StockIdOrderByBasDtDesc(String stockId);
    
    Optional<StockPrice> findByStockMaster_StockIdAndBasDt(String stockId, String basDt);
    
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stockMaster.stockId = :stockId ORDER BY sp.basDt DESC")
    List<StockPrice> findLatestPricesByStockId(@Param("stockId") String stockId, org.springframework.data.domain.Pageable pageable);
}

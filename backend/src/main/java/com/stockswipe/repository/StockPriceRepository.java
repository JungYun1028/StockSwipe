package com.stockswipe.repository;

import com.stockswipe.model.StockMaster;
import com.stockswipe.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    
    /**
     * 특정 종목의 특정 날짜 주가 조회
     */
    Optional<StockPrice> findByStockMasterAndBasDt(StockMaster stockMaster, String basDt);
    
    /**
     * 특정 종목의 모든 주가 (날짜순 정렬)
     */
    List<StockPrice> findByStockMasterOrderByBasDtDesc(StockMaster stockMaster);
    
    /**
     * 특정 종목의 최신 주가
     */
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stockMaster = :stockMaster ORDER BY sp.basDt DESC LIMIT 1")
    Optional<StockPrice> findLatestByStockMaster(StockMaster stockMaster);
    
    /**
     * 모든 종목의 최신 주가 (각 종목별로 가장 최근 날짜)
     */
    @Query("SELECT sp FROM StockPrice sp WHERE sp.basDt = " +
           "(SELECT MAX(sp2.basDt) FROM StockPrice sp2 WHERE sp2.stockMaster = sp.stockMaster)")
    List<StockPrice> findAllLatestPrices();
}

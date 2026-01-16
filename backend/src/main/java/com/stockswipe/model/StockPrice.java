package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 종목 일별 주가 정보 (날짜별로 변하는 정보)
 * - 기준일자(basDt)별로 주가 데이터 저장
 * - UNIQUE(stock_master_id, basDt): 같은 종목의 같은 날짜는 1개만
 */
@Entity
@Table(
    name = "stock_prices",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stock_master_id", "bas_dt"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_master_id", nullable = false)
    private StockMaster stockMaster;
    
    // ===== 공공 API 데이터 =====
    @Column(name = "bas_dt", nullable = false, length = 8)
    private String basDt;           // 기준일자 (YYYYMMDD)
    
    @Column(length = 20)
    private String isinCd;          // 국제채권식별번호
    
    @Column(length = 10)
    private String mrktCtg;         // 시장구분 (KOSPI, KOSDAQ, KONEX)
    
    private Long clpr;              // 종가 (Closing Price)
    private Long vs;                // 전일대비 (Versus)
    private Double fltRt;           // 등락률 (Fluctuation Rate)
    private Long mkp;               // 시가 (Market Price)
    private Long hipr;              // 고가 (High Price)
    private Long lopr;              // 저가 (Low Price)
    private Long trqu;              // 거래량 (Trade Quantity)
    private Long trPrc;             // 거래대금 (Trade Price)
    private Long lstgStCnt;         // 상장주식수 (Listing Stock Count)
    private Long mrktTotAmt;        // 시가총액 (Market Total Amount)
    
    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    
    @Column(nullable = false)
    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();
    
    // ===== 편의 메서드 =====
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    public StockPrice(StockMaster stockMaster, String basDt) {
        this.stockMaster = stockMaster;
        this.basDt = basDt;
    }
}

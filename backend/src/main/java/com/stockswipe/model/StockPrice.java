package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "stock_prices", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "bas_dt"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockMaster stockMaster;
    
    @Column(name = "bas_dt", nullable = false)
    private String basDt;           // 기준일자 (YYYYMMDD)
    
    private String isinCd;          // 국제채권식별번호
    private String mrktCtg;         // 시장구분 (KOSPI, KOSDAQ, KONEX)
    private Long clpr;              // 종가
    private Long vs;                // 전일대비
    private Double fltRt;           // 등락률
    private Long mkp;               // 시가
    private Long hipr;              // 고가
    private Long lopr;              // 저가
    private Long trqu;              // 거래량
    private Long trPrc;             // 거래대금
    private Long lstgStCnt;         // 상장주식수
    private Long mrktTotAmt;        // 시가총액
}

package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String stockId;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // ===== 공공 API 데이터 =====
    private String basDt;           // 기준일자
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
    
    // ===== OpenAI 생성 데이터 =====
    @Column(length = 1000)
    private String description;     // 기업 개요
    
    @Column(length = 1000)
    private String business;        // 사업 내용
    
    // ===== 관계 데이터 =====
    @ElementCollection
    @CollectionTable(name = "stock_keywords", joinColumns = @JoinColumn(name = "stock_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChartData> chartData = new HashSet<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> news = new ArrayList<>();
}

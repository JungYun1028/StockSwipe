package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 종목 기본 정보 (변하지 않는 정보)
 * - 종목코드, 종목명, 카테고리
 * - OpenAI 생성 정보 (기업 개요, 사업 내용, 키워드)
 */
@Entity
@Table(name = "stock_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String stockId;  // 종목코드 (예: 005930)
    
    @Column(nullable = false, length = 100)
    private String name;     // 종목명 (예: 삼성전자)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    // ===== OpenAI 생성 데이터 =====
    @Column(length = 1000)
    private String description;     // 기업 개요
    
    @Column(length = 1000)
    private String business;        // 사업 내용
    
    @Column(length = 10)
    private String analystRating;   // AI 전문가 의견: BUY, HOLD, SELL
    
    @Column(length = 500)
    private String analystReason;   // AI 전문가 의견 근거
    
    // ===== 키워드 =====
    @ElementCollection
    @CollectionTable(name = "stock_keywords", joinColumns = @JoinColumn(name = "stock_id"))
    @Column(name = "keyword", length = 50)
    private List<String> keywords = new ArrayList<>();
    
    // ===== 관계 데이터 =====
    @OneToMany(mappedBy = "stockMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChartData> chartData = new HashSet<>();
    
    @OneToMany(mappedBy = "stockMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> news = new ArrayList<>();
    
    @OneToMany(mappedBy = "stockMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> prices = new ArrayList<>();
    
    // ===== 편의 메서드 =====
    public StockMaster(String stockId, String name, Category category) {
        this.stockId = stockId;
        this.name = name;
        this.category = category;
    }
}

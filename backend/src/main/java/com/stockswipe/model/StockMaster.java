package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMaster {
    
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
    
    // ===== OpenAI 생성 데이터 =====
    @Column(length = 1000)
    private String description;     // 기업 개요
    
    @Column(length = 1000)
    private String business;        // 사업 내용
    
    // ===== 전문가 분석 =====
    @Column(length = 50)
    private String analystRating;   // 전문가 의견 (매수/중립/매도)
    
    @Column(length = 500)
    private String analystReason;    // 전문가 의견 상세
    
    // ===== 관계 데이터 =====
    @ElementCollection
    @CollectionTable(name = "stock_keywords", joinColumns = @JoinColumn(name = "stock_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
    
    @OneToMany(mappedBy = "stockMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> news = new ArrayList<>();
    
    @OneToMany(mappedBy = "stockMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> stockPrices = new ArrayList<>();
}

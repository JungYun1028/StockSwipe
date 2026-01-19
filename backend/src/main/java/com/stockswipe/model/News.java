package com.stockswipe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(exclude = "stockMaster")
@lombok.ToString(exclude = "stockMaster")
public class News {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String newsId;
    private String title;
    
    @Column(length = 1000)
    private String summary;
    
    @Column(length = 500)
    private String link;      // 뉴스 링크
    
    @Column(length = 100)
    private String source;    // 뉴스 출처 (예: 연합뉴스, 조선일보)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_master_id")
    @JsonIgnore
    private StockMaster stockMaster;
    
    public News(String newsId, String title, String summary) {
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
    }
    
    public News(String newsId, String title, String summary, String link, String source) {
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.source = source;
    }
}


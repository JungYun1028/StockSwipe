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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_master_id")
    @JsonIgnore
    private StockMaster stockMaster;
    
    public News(String newsId, String title, String summary) {
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
    }
}


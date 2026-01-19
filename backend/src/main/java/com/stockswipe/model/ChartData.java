package com.stockswipe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chart_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(exclude = "stockMaster")
@lombok.ToString(exclude = "stockMaster")
public class ChartData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String time;
    private Double price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_master_id")
    @JsonIgnore
    private StockMaster stockMaster;
    
    public ChartData(String time, Double price) {
        this.time = time;
        this.price = price;
    }
}


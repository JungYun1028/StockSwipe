package com.stockswipe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;  // bio, ai, ship, food, energy, semi, finance, battery
    
    @Column(nullable = false)
    private String name;  // 바이오, AI, 선박, 식품, 에너지, 반도체, 금융, 2차전지
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<StockMaster> stockMasters = new ArrayList<>();
    
    public Category(String code, String name) {
        this.code = code;
        this.name = name;
    }
}


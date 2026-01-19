package com.stockswipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private String id;
    private String title;
    private String summary;
    private String link;      // 뉴스 링크
    private String source;    // 뉴스 출처
    private String sentiment; // 감성 분석: POSITIVE(호재), NEGATIVE(악재), NEUTRAL(중립)
    private Double sentimentScore; // 감성 점수 (0.0 ~ 1.0)
}


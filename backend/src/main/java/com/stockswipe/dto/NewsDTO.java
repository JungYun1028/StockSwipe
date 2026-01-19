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
}


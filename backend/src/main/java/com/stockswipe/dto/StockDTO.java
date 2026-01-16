package com.stockswipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String id;
    private String name;
    private CategoryDTO category;
    
    // 공공 API 데이터
    private String basDt;           // 기준일자
    private String isinCd;          // 국제채권식별번호
    private String mrktCtg;         // 시장구분
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
    
    // OpenAI 생성 데이터
    private String description;     // 기업 개요
    private String business;        // 사업 내용
    
    // 관계 데이터
    private List<String> keywords;
    private List<ChartDataDTO> chartData;
    private List<NewsDTO> news;
}

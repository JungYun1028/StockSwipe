package com.stockswipe.service;

import com.stockswipe.model.ChartData;
import com.stockswipe.model.Stock;
import com.stockswipe.model.StockPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 기술적 지표 계산 서비스
 * RSI, 이동평균선 등을 계산합니다.
 */
@Slf4j
@Service
public class TechnicalIndicatorService {
    
    /**
     * RSI (Relative Strength Index) 계산
     * 14일 기준으로 계산합니다.
     */
    public Double calculateRSI(Stock stock) {
        List<ChartData> chartData = stock.getChartData().stream()
                .sorted(Comparator.comparing(ChartData::getTime))
                .collect(Collectors.toList());
        
        if (chartData.size() < 15) {
            // 데이터가 부족하면 기본값 반환
            return 50.0;
        }
        
        List<Double> prices = chartData.stream()
                .map(ChartData::getPrice)
                .collect(Collectors.toList());
        
        // 가격 변화량 계산
        List<Double> changes = new java.util.ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            changes.add(prices.get(i) - prices.get(i - 1));
        }
        
        // 최근 14일 데이터 사용
        int period = 14;
        if (changes.size() < period) {
            return 50.0;
        }
        
        List<Double> recentChanges = changes.subList(changes.size() - period, changes.size());
        
        // 상승분과 하락분 분리
        double avgGain = 0.0;
        double avgLoss = 0.0;
        
        for (Double change : recentChanges) {
            if (change > 0) {
                avgGain += change;
            } else {
                avgLoss += Math.abs(change);
            }
        }
        
        avgGain /= period;
        avgLoss /= period;
        
        if (avgLoss == 0) {
            return 100.0;
        }
        
        double rs = avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        
        return Math.round(rsi * 100.0) / 100.0; // 소수점 2자리
    }
    
    /**
     * 이동평균선 계산
     * @param stock 종목
     * @param period 기간 (20, 60, 120 등)
     * @return 이동평균값
     */
    public Double calculateMovingAverage(Stock stock, int period) {
        List<ChartData> chartData = stock.getChartData().stream()
                .sorted(Comparator.comparing(ChartData::getTime))
                .collect(Collectors.toList());
        
        if (chartData.size() < period) {
            // 데이터가 부족하면 현재가 기반으로 추정
            if (stock.getClpr() != null) {
                return stock.getClpr().doubleValue();
            }
            return 0.0;
        }
        
        // 최근 period일의 평균 계산
        List<Double> recentPrices = chartData.stream()
                .skip(chartData.size() - period)
                .map(ChartData::getPrice)
                .collect(Collectors.toList());
        
        double sum = recentPrices.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        double ma = sum / period;
        return Math.round(ma * 100.0) / 100.0; // 소수점 2자리
    }
    
    /**
     * RSI 계산 (StockPrice 리스트 사용)
     */
    public Double calculateRSIFromPrices(List<StockPrice> prices) {
        if (prices == null || prices.size() < 15) {
            return 50.0;
        }
        
        // basDt 기준으로 정렬 (오름차순)
        List<StockPrice> sortedPrices = prices.stream()
                .sorted((p1, p2) -> p1.getBasDt().compareTo(p2.getBasDt()))
                .collect(Collectors.toList());
        
        List<Double> priceValues = sortedPrices.stream()
                .filter(p -> p.getClpr() != null)
                .map(p -> p.getClpr().doubleValue())
                .collect(Collectors.toList());
        
        if (priceValues.size() < 15) {
            return 50.0;
        }
        
        // 가격 변화량 계산
        List<Double> changes = new java.util.ArrayList<>();
        for (int i = 1; i < priceValues.size(); i++) {
            changes.add(priceValues.get(i) - priceValues.get(i - 1));
        }
        
        int period = 14;
        if (changes.size() < period) {
            return 50.0;
        }
        
        List<Double> recentChanges = changes.subList(changes.size() - period, changes.size());
        
        double avgGain = 0.0;
        double avgLoss = 0.0;
        
        for (Double change : recentChanges) {
            if (change > 0) {
                avgGain += change;
            } else {
                avgLoss += Math.abs(change);
            }
        }
        
        avgGain /= period;
        avgLoss /= period;
        
        if (avgLoss == 0) {
            return 100.0;
        }
        
        double rs = avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        
        return Math.round(rsi * 100.0) / 100.0;
    }
    
    /**
     * 이동평균선 계산 (StockPrice 리스트 사용)
     */
    public Double calculateMovingAverageFromPrices(List<StockPrice> prices, int period) {
        if (prices == null || prices.size() < period) {
            return null;
        }
        
        // basDt 기준으로 정렬 (오름차순)
        List<StockPrice> sortedPrices = prices.stream()
                .sorted((p1, p2) -> p1.getBasDt().compareTo(p2.getBasDt()))
                .collect(Collectors.toList());
        
        // 최근 period일의 평균 계산
        List<Double> recentPrices = sortedPrices.stream()
                .skip(Math.max(0, sortedPrices.size() - period))
                .filter(p -> p.getClpr() != null)
                .map(p -> p.getClpr().doubleValue())
                .collect(Collectors.toList());
        
        if (recentPrices.size() < period) {
            return null;
        }
        
        double sum = recentPrices.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        double ma = sum / period;
        return Math.round(ma * 100.0) / 100.0;
    }
    
    /**
     * RSI 상태 설명 반환
     */
    public String getRSIStatus(Double rsi) {
        if (rsi == null) {
            return "중립";
        }
        if (rsi >= 70) {
            return "과매수";
        } else if (rsi <= 30) {
            return "과매도";
        } else {
            return "중립";
        }
    }
}

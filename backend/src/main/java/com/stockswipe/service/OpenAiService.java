package com.stockswipe.service;

import com.stockswipe.model.StockMaster;
import com.stockswipe.repository.StockMasterRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAiService {

    private final StockMasterRepository stockMasterRepository;
    private final com.theokanning.openai.service.OpenAiService openAiClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public OpenAiService(StockMasterRepository stockMasterRepository, @Value("${openai.api.key}") String apiKey) {
        this.stockMasterRepository = stockMasterRepository;
        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-openai-api-key-here")) {
            this.openAiClient = new com.theokanning.openai.service.OpenAiService(apiKey, Duration.ofSeconds(60));
        } else {
            this.openAiClient = null;
            log.warn("⚠️ OpenAI API 키가 설정되지 않았습니다. AI 기능이 비활성화됩니다.");
        }
    }

    /**
     * 특정 종목의 기업 개요와 키워드를 생성
     */
    @Transactional
    public void generateStockInfo(String stockId) {
        if (openAiClient == null) {
            log.warn("OpenAI 서비스가 초기화되지 않았습니다.");
            return;
        }

        StockMaster stockMaster = stockMasterRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("StockMaster not found: " + stockId));

        try {
            // 1. 기업 개요 생성
            String descriptionPrompt = String.format(
                    "한국 주식 종목 '%s'에 대한 간단한 기업 개요를 2-3문장으로 작성해주세요. 객관적이고 간결하게 설명해주세요.",
                    stockMaster.getName()
            );
            String description = callOpenAI(descriptionPrompt);
            stockMaster.setDescription(description);

            // 2. 사업 내용 생성
            String businessPrompt = String.format(
                    "한국 주식 종목 '%s'의 주요 사업 내용을 1-2문장으로 작성해주세요.",
                    stockMaster.getName()
            );
            String business = callOpenAI(businessPrompt);
            stockMaster.setBusiness(business);

            // 3. 키워드 5개 생성
            String keywordPrompt = String.format(
                    "한국 주식 종목 '%s'와 관련된 핵심 키워드 5개를 쉼표로 구분하여 나열해주세요. 예: AI, 검색, 플랫폼, 클라우드, 커머스",
                    stockMaster.getName()
            );
            String keywordsResponse = callOpenAI(keywordPrompt);
            List<String> keywords = parseKeywords(keywordsResponse);
            stockMaster.setKeywords(keywords);

            stockMasterRepository.save(stockMaster);
            log.info("✅ {} OpenAI 정보 생성 완료", stockMaster.getName());

        } catch (Exception e) {
            log.error("❌ {} OpenAI 정보 생성 실패: {}", stockMaster.getName(), e.getMessage());
        }
    }

    /**
     * 모든 종목의 기업 개요와 키워드를 생성
     */
    public void generateAllStocksInfo() {
        if (openAiClient == null) {
            log.warn("OpenAI 서비스가 초기화되지 않았습니다.");
            return;
        }

        List<StockMaster> stockMasters = stockMasterRepository.findAll();
        log.info("📊 총 {}개 종목의 AI 정보를 생성합니다...", stockMasters.size());

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < stockMasters.size(); i++) {
            StockMaster stockMaster = stockMasters.get(i);
            try {
                generateStockInfo(stockMaster.getStockId());
                successCount++;
                log.info("✅ [{}/{}] {} AI 정보 생성 완료", i + 1, stockMasters.size(), stockMaster.getName());

                // API 호출 제한 방지 (RPM 제한 고려)
                Thread.sleep(1000); // 1초 대기

            } catch (Exception e) {
                failCount++;
                log.error("❌ [{}/{}] {} AI 정보 생성 실패: {}", i + 1, stockMasters.size(), stockMaster.getName(), e.getMessage());
            }
        }

        log.info("🎉 AI 정보 생성 완료! 성공: {}, 실패: {}", successCount, failCount);
    }

    /**
     * OpenAI API 호출
     */
    private String callOpenAI(String prompt) {
        if (openAiClient == null) {
            return "OpenAI API 키가 설정되지 않았습니다.";
        }

        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "당신은 한국 주식 시장 전문가입니다."));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .maxTokens(300)
                    .temperature(0.7)
                    .build();

            var response = openAiClient.createChatCompletion(request);
            return response.getChoices().get(0).getMessage().getContent().trim();

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                return "OpenAI API 키가 유효하지 않습니다. API 키를 확인해주세요.";
            } else if (e.getMessage() != null && e.getMessage().contains("429")) {
                return "OpenAI API 호출 한도가 초과되었습니다. 잠시 후 다시 시도해주세요.";
            }
            return "OpenAI API 호출 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /**
     * 키워드 문자열을 파싱하여 리스트로 변환
     */
    private List<String> parseKeywords(String keywordsResponse) {
        List<String> keywords = new ArrayList<>();
        String[] parts = keywordsResponse.split("[,،、]"); // 쉼표, 아랍 쉼표, 중국 쉼표
        for (String part : parts) {
            String keyword = part.trim();
            if (!keyword.isEmpty() && keywords.size() < 5) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    /**
    /**
     * 포트폴리오 분석 및 조언 생성 (main 브랜치 기능)
     */
    public String analyzePortfolio(List<String> stockNames, List<String> categories, 
                                   double avgReturn, int upStocks, int downStocks, 
                                   Map<String, Integer> sectorDistribution) {
        if (openAiClient == null) {
            log.warn("OpenAI 클라이언트가 초기화되지 않았습니다. API 키를 확인해주세요.");
            return "OpenAI API 키가 설정되지 않아 포트폴리오 분석을 수행할 수 없습니다.";
        }

        try {
            log.info("포트폴리오 분석 시작 - 종목 수: {}, 섹터 수: {}", stockNames.size(), categories.size());
            
            StringBuilder prompt = new StringBuilder();
            prompt.append("다음은 사용자의 주식 포트폴리오 정보입니다:\n\n");
            
            if (stockNames != null && !stockNames.isEmpty()) {
                prompt.append("관심 종목: ").append(String.join(", ", stockNames)).append("\n");
            } else {
                prompt.append("관심 종목: 없음\n");
            }
            
            if (categories != null && !categories.isEmpty()) {
                prompt.append("섹터: ").append(String.join(", ", categories)).append("\n");
            } else {
                prompt.append("섹터: 없음\n");
            }
            
            prompt.append("평균 수익률: ").append(String.format("%.2f", avgReturn)).append("%\n");
            prompt.append("상승 종목: ").append(upStocks).append("개\n");
            prompt.append("하락 종목: ").append(downStocks).append("개\n");
            
            if (sectorDistribution != null && !sectorDistribution.isEmpty()) {
                prompt.append("섹터별 분포: ");
                sectorDistribution.forEach((sector, count) -> {
                    prompt.append(sector).append("(").append(count).append("개), ");
                });
                prompt.append("\n");
            }
            
            prompt.append("\n");
            prompt.append("이 포트폴리오를 분석하여 다음을 포함한 조언을 3-4문장으로 작성해주세요:\n");
            prompt.append("1. 포트폴리오의 강점과 약점\n");
            prompt.append("2. 분산투자 관점에서의 평가\n");
            prompt.append("3. 개선 방안 또는 유지 권장 사항\n");
            prompt.append("한국어로 간결하고 실용적인 조언을 제공해주세요.");

            String result = callOpenAI(prompt.toString());
            log.info("포트폴리오 분석 완료");
            return result;

        } catch (Exception e) {
            log.error("포트폴리오 분석 실패: {}", e.getMessage(), e);
            return "포트폴리오 분석 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /**
     * 챗봇 대화 - 사용자 질문에 대한 AI 답변 생성 (develop 브랜치 기능)
     */
    public String chat(String userMessage, String stockContext) {
        if (openAiClient == null) {
            return "죄송합니다. OpenAI 서비스가 초기화되지 않았습니다. API 키를 확인해주세요.";
        }

        try {
            List<ChatMessage> messages = new ArrayList<>();
            
            // 시스템 프롬프트 - 한국 주식 전문가 역할
            String systemPrompt = "당신은 한국 주식 시장 전문가이자 친절한 투자 어시스턴트입니다. " +
                    "사용자의 주식 투자 관련 질문에 명확하고 이해하기 쉽게 답변해주세요. " +
                    "기술적 지표(RSI, 이동평균 등), 투자 전략, 시장 용어 등을 설명할 때는 초보자도 이해할 수 있도록 친절하게 설명해주세요. " +
                    "투자 권유는 하지 말고, 정보와 분석만 제공하며, 최종 투자 결정은 개인의 책임임을 강조해주세요.";
            
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
            
            // 종목 컨텍스트가 있으면 추가
            if (stockContext != null && !stockContext.isEmpty()) {
                messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                        "현재 사용자가 보고 있는 종목 정보: " + stockContext));
            }
            
            // 사용자 메시지
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .maxTokens(500)
                    .temperature(0.7)
                    .build();

            var response = openAiClient.createChatCompletion(request);
            String answer = response.getChoices().get(0).getMessage().getContent().trim();
            
            log.info("✅ 챗봇 응답 생성 완료");
            return answer;

        } catch (Exception e) {
            log.error("❌ 챗봇 응답 생성 실패: {}", e.getMessage());
            return "죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
    
    /**
     * 뉴스 감성 기반 AI 전문가 분석 생성
     * - 최신 뉴스 10개 분석
     * - 5개 이상 호재 → 매수 추천
     * - 3개 이상 악재 → 중립/관망 추천
     */
    @Transactional
    public void generateAnalystRating(String stockId) {
        StockMaster stockMaster = stockMasterRepository.findByStockId(stockId)
                .orElseThrow(() -> new RuntimeException("StockMaster not found: " + stockId));
        
        // 최신 뉴스 10개 가져오기
        List<com.stockswipe.model.News> recentNews = stockMaster.getNews().stream()
                .sorted((n1, n2) -> Long.compare(n2.getId(), n1.getId())) // ID 기준 내림차순
                .limit(10)
                .toList();
        
        if (recentNews.isEmpty()) {
            log.info("⚠️ {} 뉴스가 없어서 AI 분석을 생략합니다.", stockMaster.getName());
            return;
        }
        
        // 호재/악재 카운트
        long positiveCount = recentNews.stream()
                .filter(n -> "POSITIVE".equals(n.getSentiment()))
                .count();
        long negativeCount = recentNews.stream()
                .filter(n -> "NEGATIVE".equals(n.getSentiment()))
                .count();
        
        log.info("📊 {} 뉴스 감성 분석 - 호재: {}개, 악재: {}개 (총 {}개)", 
                stockMaster.getName(), positiveCount, negativeCount, recentNews.size());
        
        // AI 전문가 의견 결정
        String rating;
        String reason;
        
        if (positiveCount >= 5) {
            rating = "BUY";
            reason = String.format("최근 %d개 뉴스 중 %d개가 호재로, 긍정적인 흐름이 강합니다. 적극 매수를 고려해볼 만합니다.", 
                    recentNews.size(), positiveCount);
        } else if (negativeCount >= 3) {
            rating = "HOLD";
            reason = String.format("최근 %d개 뉴스 중 %d개가 악재로, 부정적인 요소가 있습니다. 관망 또는 신중한 접근이 필요합니다.", 
                    recentNews.size(), negativeCount);
        } else {
            rating = "HOLD";
            reason = String.format("최근 %d개 뉴스의 감성이 혼재되어 있습니다. 추가 정보를 확인한 후 투자를 결정하시기 바랍니다.", 
                    recentNews.size());
        }
        
        stockMaster.setAnalystRating(rating);
        stockMaster.setAnalystReason(reason);
        stockMasterRepository.save(stockMaster);
        
        log.info("✅ {} AI 전문가 분석 완료 - {}: {}", stockMaster.getName(), rating, reason);
    }
    
    /**
     * 뉴스 감성 분석 (호재/악재 판단)
     * @param stockName 종목명
     * @param newsTitle 뉴스 제목
     * @param newsSummary 뉴스 요약
     * @return Map with "sentiment" (POSITIVE/NEGATIVE/NEUTRAL) and "score" (0.0~1.0)
     */
    public Map<String, Object> analyzeNewsSentiment(String stockName, String newsTitle, String newsSummary) {
        if (openAiClient == null) {
            log.warn("⚠️ OpenAI 클라이언트가 초기화되지 않았습니다. 기본값 NEUTRAL 반환");
            return Map.of("sentiment", "NEUTRAL", "score", 0.5);
        }
        
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("당신은 주식 뉴스 감성 분석 전문가입니다.\n\n");
            prompt.append("종목명: ").append(stockName).append("\n");
            prompt.append("뉴스 제목: ").append(newsTitle).append("\n");
            if (newsSummary != null && !newsSummary.isEmpty()) {
                prompt.append("뉴스 요약: ").append(newsSummary).append("\n");
            }
            prompt.append("\n");
            prompt.append("위 뉴스가 해당 종목에 미치는 영향을 분석하여 다음 중 하나로 분류해주세요:\n");
            prompt.append("- POSITIVE: 호재 (주가 상승에 긍정적)\n");
            prompt.append("- NEGATIVE: 악재 (주가 하락에 부정적)\n");
            prompt.append("- NEUTRAL: 중립 (영향 미미하거나 불명확)\n\n");
            prompt.append("응답 형식: [감성]|[점수]\n");
            prompt.append("예시: POSITIVE|0.85\n");
            prompt.append("점수는 0.0(매우 부정) ~ 1.0(매우 긍정) 범위로 제공해주세요.");
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt.toString()));
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .maxTokens(50)
                    .temperature(0.3)
                    .build();
            
            String response = openAiClient.createChatCompletion(completionRequest)
                    .getChoices().get(0).getMessage().getContent().trim();
            
            log.info("📊 뉴스 감성 분석 결과: {}", response);
            
            // 응답 파싱: "POSITIVE|0.85" 형식
            String[] parts = response.split("\\|");
            String sentiment = parts.length > 0 ? parts[0].trim().toUpperCase() : "NEUTRAL";
            double score = parts.length > 1 ? Double.parseDouble(parts[1].trim()) : 0.5;
            
            // sentiment 값 검증
            if (!sentiment.equals("POSITIVE") && !sentiment.equals("NEGATIVE") && !sentiment.equals("NEUTRAL")) {
                sentiment = "NEUTRAL";
                score = 0.5;
            }
            
            return Map.of("sentiment", sentiment, "score", score);
            
        } catch (Exception e) {
            log.error("❌ 뉴스 감성 분석 실패: {}", e.getMessage());
            return Map.of("sentiment", "NEUTRAL", "score", 0.5);
        }
    }
}

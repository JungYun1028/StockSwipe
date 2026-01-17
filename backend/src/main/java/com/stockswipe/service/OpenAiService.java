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
                    .maxTokens(200)
                    .temperature(0.7)
                    .build();

            var response = openAiClient.createChatCompletion(request);
            return response.getChoices().get(0).getMessage().getContent().trim();

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            return "정보를 가져올 수 없습니다.";
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
     * 챗봇 대화 - 사용자 질문에 대한 AI 답변 생성
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
}


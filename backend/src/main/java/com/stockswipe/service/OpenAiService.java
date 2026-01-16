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
}


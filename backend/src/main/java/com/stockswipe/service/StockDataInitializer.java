package com.stockswipe.service;

import com.stockswipe.model.*;
import com.stockswipe.repository.CategoryRepository;
import com.stockswipe.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class StockDataInitializer implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            System.out.println("✅ 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }
        
        System.out.println("📊 카테고리 및 종목 데이터 초기화 시작...");
        initializeCategories();
        System.out.println("✅ 데이터 초기화 완료!");
    }
    
    private void initializeCategories() {
        // 8개 카테고리 생성
        Category bio = createCategory("bio", "바이오");
        Category ai = createCategory("ai", "AI");
        Category ship = createCategory("ship", "선박");
        Category food = createCategory("food", "식품");
        Category energy = createCategory("energy", "에너지");
        Category semi = createCategory("semi", "반도체");
        Category finance = createCategory("finance", "금융");
        Category battery = createCategory("battery", "2차전지");
        
        // 각 카테고리별 종목 생성 (실제 종목코드 사용)
        createBioStocks(bio);
        createAIStocks(ai);
        createShipStocks(ship);
        createFoodStocks(food);
        createEnergyStocks(energy);
        createSemiStocks(semi);
        createFinanceStocks(finance);
        createBatteryStocks(battery);
    }
    
    private Category createCategory(String code, String name) {
        Category category = new Category(code, name);
        return categoryRepository.save(category);
    }
    
    // 바이오 종목 20개
    private void createBioStocks(Category category) {
        String[][] bioStocks = {
            {"셀트리온", "068270"}, {"삼성바이오로직스", "207940"},
            {"셀트리온헬스케어", "091990"}, {"SK바이오팜", "326030"},
            {"유한양행", "000100"}, {"한미약품", "128940"},
            {"대웅제약", "069620"}, {"종근당", "185750"},
            {"녹십자", "006280"}, {"JW중외제약", "001060"},
            {"신풍제약", "019170"}, {"일양약품", "007570"},
            {"동아에스티", "170900"}, {"부광약품", "003000"},
            {"HK이노엔", "195940"}, {"유틸렉스", "263050"},
            {"메지온", "140410"}, {"차바이오텍", "085660"},
            {"바이오니아", "064550"}, {"제넥신", "095700"}
        };
        createStocksForCategory(category, bioStocks);
    }
    
    // AI 종목 20개
    private void createAIStocks(Category category) {
        String[][] aiStocks = {
            {"네이버", "035420"}, {"카카오", "035720"},
            {"LG전자", "066570"}, {"셀바스AI", "108860"},
            {"솔트룩스", "304100"}, {"한글과컴퓨터", "030520"},
            {"수아랩", "368770"}, {"씨앤에이아이", "330350"},
            {"딥노이드", "315640"}, {"뷰웍스", "100120"},
            {"인텔리안테크", "189300"}, {"코난테크놀로지", "226340"},
            {"엔씨소프트", "036570"}, {"넷마블", "251270"},
            {"크래프톤", "259960"}, {"카카오게임즈", "293490"},
            {"카카오뱅크", "323410"}, {"위메이드", "112040"},
            {"컴투스", "078340"}, {"NHN", "181710"}
        };
        createStocksForCategory(category, aiStocks);
    }
    
    // 선박 종목 20개
    private void createShipStocks(Category category) {
        String[][] shipStocks = {
            {"HD한국조선해양", "009540"}, {"삼성중공업", "010140"},
            {"한화오션", "042660"}, {"현대미포조선", "010620"},
            {"대한조선", "000430"}, {"HJ중공업", "103590"},
            {"삼양홀딩스", "000070"}, {"팬오션", "028670"},
            {"흥아해운", "003280"}, {"대한해운", "005880"},
            {"KSS해운", "044450"}, {"고려해운", "000540"},
            {"SM상선", "019570"}, {"CJ대한통운", "000120"},
            {"현대글로비스", "086280"}, {"한진", "002320"},
            {"한진칼", "180640"}, {"시노펙스", "025320"},
            {"남성", "004270"}, {"대선조선", "001430"}
        };
        createStocksForCategory(category, shipStocks);
    }
    
    // 식품 종목 20개
    private void createFoodStocks(Category category) {
        String[][] foodStocks = {
            {"CJ제일제당", "097950"}, {"오뚜기", "007310"},
            {"농심", "004370"}, {"롯데제과", "280360"},
            {"오리온", "271560"}, {"동원F&B", "049770"},
            {"하림지주", "003380"}, {"사조대림", "003960"},
            {"대상", "001680"}, {"삼양식품", "003230"},
            {"빙그레", "005180"}, {"남양유업", "003920"},
            {"매일유업", "267980"}, {"동서", "026960"},
            {"SPC삼립", "005610"}, {"파리크라상", "008040"},
            {"CJ푸드빌", "007980"}, {"신세계푸드", "031440"},
            {"풀무원", "017810"}, {"한국야쿠르트", "004000"}
        };
        createStocksForCategory(category, foodStocks);
    }
    
    // 에너지 종목 20개
    private void createEnergyStocks(Category category) {
        String[][] energyStocks = {
            {"한국전력", "015760"}, {"SK이노베이션", "096770"},
            {"GS", "078930"}, {"S-Oil", "010950"},
            {"한화", "000880"}, {"두산에너빌리티", "034020"},
            {"한국가스공사", "036460"}, {"GS글로벌", "001250"},
            {"SK가스", "018670"}, {"E1", "017940"},
            {"한화에어로스페이스", "012450"}, {"두산퓨얼셀", "336260"},
            {"에스퓨얼셀", "288620"}, {"블룸비츠", "200470"},
            {"신성이엔지", "011930"}, {"한국전력기술", "052690"},
            {"한전KPS", "051600"}, {"GS건설", "006360"},
            {"현대건설", "000720"}, {"대림산업", "000210"}
        };
        createStocksForCategory(category, energyStocks);
    }
    
    // 반도체 종목 20개
    private void createSemiStocks(Category category) {
        String[][] semiStocks = {
            {"삼성전자", "005930"}, {"SK하이닉스", "000660"},
            {"DB하이텍", "000990"}, {"한미반도체", "042700"},
            {"원익IPS", "240810"}, {"유진테크", "084370"},
            {"주성엔지니어링", "036930"}, {"AP시스템", "265520"},
            {"테스", "095610"}, {"케이씨텍", "281820"},
            {"파크시스템스", "140860"}, {"이오테크닉스", "039030"},
            {"원익QnC", "074600"}, {"SFA반도체", "036540"},
            {"티씨케이", "064760"}, {"LX세미콘", "108320"},
            {"엘비세미콘", "061970"}, {"하나마이크론", "067310"},
            {"네패스", "033640"}, {"SK실트론", "222800"}
        };
        createStocksForCategory(category, semiStocks);
    }
    
    // 금융 종목 20개
    private void createFinanceStocks(Category category) {
        String[][] financeStocks = {
            {"삼성생명", "032830"}, {"KB금융", "105560"},
            {"신한지주", "055550"}, {"하나금융지주", "086790"},
            {"우리금융지주", "316140"}, {"JB금융지주", "175330"},
            {"DGB금융지주", "139130"}, {"BNK금융지주", "138930"},
            {"삼성화재", "000810"}, {"DB손해보험", "005830"},
            {"한화생명", "088350"}, {"미래에셋증권", "006800"},
            {"삼성증권", "016360"}, {"한국투자증권", "003470"},
            {"NH투자증권", "005940"}, {"키움증권", "039490"},
            {"한국금융지주", "071050"}, {"메리츠금융지주", "138040"},
            {"교보증권", "030610"}, {"하이투자증권", "003560"}
        };
        createStocksForCategory(category, financeStocks);
    }
    
    // 2차전지 종목 20개
    private void createBatteryStocks(Category category) {
        String[][] batteryStocks = {
            {"LG에너지솔루션", "373220"}, {"삼성SDI", "006400"},
            {"SK온", "402340"}, {"포스코퓨처엠", "003670"},
            {"에코프로비엠", "247540"}, {"에코프로", "086520"},
            {"L&F", "066970"}, {"천보", "278280"},
            {"코스모신소재", "005070"}, {"피엔티", "137400"},
            {"대주전자재료", "078600"}, {"후성", "093370"},
            {"솔루스첨단소재", "336370"}, {"일진머티리얼즈", "020150"},
            {"SK아이이테크놀로지", "361610"}, {"상아프론테크", "089980"},
            {"켐트로닉스", "089010"}, {"펄어비스", "263750"},
            {"에코프로에이치엔", "383310"}, {"에코플라스틱", "038110"}
        };
        createStocksForCategory(category, batteryStocks);
    }
    
    private void createStocksForCategory(Category category, String[][] stockData) {
        int created = 0;
        int skipped = 0;
        
        for (String[] data : stockData) {
            String stockId = data[1];
            
            // 중복 체크: 이미 존재하는 종목은 건너뛰기
            if (stockRepository.findByStockId(stockId).isPresent()) {
                skipped++;
                continue;
            }
            
            Stock stock = new Stock();
            stock.setStockId(stockId);  // 종목코드
            stock.setName(data[0]);     // 종목명
            stock.setCategory(category);
            
            stockRepository.save(stock);
            created++;
        }
        
        System.out.println("✅ " + category.getName() + " 카테고리: " + created + "개 생성, " + skipped + "개 중복 건너뜀");
    }
}

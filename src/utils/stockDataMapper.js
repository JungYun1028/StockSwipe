/**
 * 백엔드 API 데이터를 프론트엔드 형식으로 변환
 */
export const mapStockData = (backendStock) => {
  // 전일종가 계산: 현재가 - 전일대비
  const previousClose = backendStock.clpr && backendStock.vs
    ? backendStock.clpr - backendStock.vs
    : null;
  
  // 거래량 기반으로 매수/매도량 추정 (60:40 비율)
  const estimateBuySellVolume = (totalVolume) => {
    if (!totalVolume) return { buy: 0, sell: 0 };
    const buyRatio = 0.55 + (Math.random() * 0.1); // 55-65%
    return {
      buy: Math.floor(totalVolume * buyRatio),
      sell: Math.floor(totalVolume * (1 - buyRatio)),
    };
  };
  
  const { buy, sell } = estimateBuySellVolume(backendStock.trqu);
  
  return {
    id: backendStock.id,
    name: backendStock.name,
    ticker: backendStock.id,
    category: backendStock.category ? [backendStock.category.name] : [],
    categoryCode: backendStock.category?.code,
    
    // 가격 정보
    currentPrice: backendStock.clpr || 0,
    previousClose: previousClose || backendStock.clpr || 0,
    high: backendStock.hipr || backendStock.clpr || 0,
    low: backendStock.lopr || backendStock.clpr || 0,
    
    // 거래 정보
    volume: backendStock.trqu || 0,
    buyVolume: buy,
    sellVolume: sell,
    
    // 시장 정보
    marketType: backendStock.mrktCtg,
    marketCap: backendStock.mrktTotAmt,
    
    // 차트 데이터
    chartData: backendStock.chartData || [],
    
    // 뉴스
    news: (backendStock.news || []).map(n => ({
      id: n.id || n.newsId,
      title: n.title,
      summary: n.summary,
      link: n.link,      // 구글 뉴스 링크
      source: n.source,  // 뉴스 출처
      sentiment: n.sentiment,  // 감성 분석: POSITIVE(호재), NEGATIVE(악재), NEUTRAL(중립)
      sentimentScore: n.sentimentScore,  // 감성 점수 (0.0~1.0)
    })),
    
    // 키워드
    keywords: backendStock.keywords || [],
    
    // OpenAI 생성 데이터
    description: backendStock.description,
    business: backendStock.business,
    
    // 원본 데이터도 보관
    _raw: backendStock,
  };
};

/**
 * 여러 주식 데이터를 변환
 */
export const mapStocksData = (backendStocks) => {
  return backendStocks.map(mapStockData);
};


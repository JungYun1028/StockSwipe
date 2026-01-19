import { useNavigate, Navigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, Heart, ChevronRight, PieChart, BarChart3, Award, TrendingDown as TrendingDownIcon, X, Sparkles, Loader2 } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { stockAPI } from '../services/api';
import Navigation from '../components/Navigation';
import FloatingChatbot from '../components/FloatingChatbot';
import styles from './WatchList.module.css';

const WatchList = () => {
  const navigate = useNavigate();
  const { hasCompletedOnboarding, likedStocks, removeLikedStock } = useApp();
  const [aiAdvice, setAiAdvice] = useState(null);
  const [isLoadingAdvice, setIsLoadingAdvice] = useState(false);
  
  if (!hasCompletedOnboarding) {
    return <Navigate to="/" replace />;
  }
  
  const formatPrice = (price) => price?.toLocaleString('ko-KR') || '0';
  const formatNumber = (num) => {
    if (!num) return '0';
    if (num >= 1000000000) return `${(num / 1000000000).toFixed(1)}B`;
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
    if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
    return num.toString();
  };
  
  // Group stocks by category
  const groupedStocks = likedStocks.reduce((acc, stock) => {
    const category = stock.category && stock.category.length > 0 ? stock.category[0] : '기타';
    if (!acc[category]) {
      acc[category] = [];
    }
    acc[category].push(stock);
    return acc;
  }, {});
  
  // Extract all unique keywords from liked stocks
  const allKeywords = Array.from(
    new Set(likedStocks.flatMap(stock => stock.keywords || []))
  ).slice(0, 10);

  // 포트폴리오 요약 계산
  const calculatePortfolioSummary = () => {
    if (likedStocks.length === 0) return null;

    // 각 종목의 수익률 계산
    const returns = likedStocks.map(stock => {
      const priceChange = stock.currentPrice - stock.previousClose;
      const returnPercent = stock.previousClose > 0 
        ? (priceChange / stock.previousClose) * 100 
        : 0;
      return {
        stock,
        returnPercent,
        priceChange,
      };
    });

    // 평균 수익률
    const avgReturn = returns.reduce((sum, r) => sum + r.returnPercent, 0) / returns.length;

    // 최고/최저 수익 종목
    const sortedByReturn = [...returns].sort((a, b) => b.returnPercent - a.returnPercent);
    const bestStock = sortedByReturn[0];
    const worstStock = sortedByReturn[sortedByReturn.length - 1];

    // 상승/하락 종목 수
    const upStocks = returns.filter(r => r.returnPercent > 0).length;
    const downStocks = returns.filter(r => r.returnPercent < 0).length;
    const neutralStocks = returns.filter(r => r.returnPercent === 0).length;

    // 섹터별 분포
    const sectorDistribution = Object.entries(groupedStocks).map(([category, stocks]) => ({
      category,
      count: stocks.length,
      percentage: (stocks.length / likedStocks.length) * 100,
    })).sort((a, b) => b.count - a.count);

    // 총 포트폴리오 가치 (가정: 각 종목당 100만원 투자)
    const totalValue = likedStocks.length * 1000000;
    const totalGain = returns.reduce((sum, r) => sum + (r.priceChange * 100), 0);
    const totalReturnPercent = (totalGain / totalValue) * 100;

    // 키워드 빈도 분석
    const keywordFrequency = {};
    likedStocks.forEach(stock => {
      (stock.keywords || []).forEach(keyword => {
        keywordFrequency[keyword] = (keywordFrequency[keyword] || 0) + 1;
      });
    });
    const topKeywords = Object.entries(keywordFrequency)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([keyword, count]) => ({ keyword, count }));

    return {
      totalStocks: likedStocks.length,
      totalSectors: Object.keys(groupedStocks).length,
      avgReturn,
      totalValue,
      totalGain,
      totalReturnPercent,
      upStocks,
      downStocks,
      neutralStocks,
      bestStock,
      worstStock,
      sectorDistribution,
      topKeywords,
    };
  };

  const portfolioSummary = calculatePortfolioSummary();

  // AI 포트폴리오 분석
  useEffect(() => {
    // 이미 로딩 중이면 중복 호출 방지
    if (isLoadingAdvice) return;
    
    const fetchAiAdvice = async () => {
      if (!portfolioSummary || likedStocks.length === 0) {
        setAiAdvice(null);
        return;
      }

      setIsLoadingAdvice(true);
      try {
        const stockNames = likedStocks.map(s => s.name || '알 수 없음').filter(Boolean);
        const categories = Object.keys(groupedStocks).filter(Boolean);
        const sectorDistribution = {};
        
        if (portfolioSummary.sectorDistribution && portfolioSummary.sectorDistribution.length > 0) {
          portfolioSummary.sectorDistribution.forEach(sector => {
            if (sector && sector.category) {
              sectorDistribution[sector.category] = sector.count || 0;
            }
          });
        }

        console.log('📊 포트폴리오 분석 요청:', {
          stockNames: stockNames.length,
          categories: categories.length,
          avgReturn: portfolioSummary.avgReturn,
          upStocks: portfolioSummary.upStocks,
          downStocks: portfolioSummary.downStocks,
          sectorDistribution
        });

        const response = await stockAPI.analyzePortfolio({
          stockNames,
          categories,
          avgReturn: portfolioSummary.avgReturn || 0,
          upStocks: portfolioSummary.upStocks || 0,
          downStocks: portfolioSummary.downStocks || 0,
          sectorDistribution,
        });

        console.log('✅ AI 조언 응답:', response);
        setAiAdvice(response.advice || 'AI 조언을 생성할 수 없습니다.');
      } catch (error) {
        console.error('❌ AI 조언 가져오기 실패:', error);
        console.error('에러 상세:', error.response?.data || error.message);
        setAiAdvice('AI 조언을 가져오는 중 오류가 발생했습니다. ' + (error.response?.data?.message || error.message));
      } finally {
        setIsLoadingAdvice(false);
      }
    };

    fetchAiAdvice();
  }, [likedStocks.length]); // 종목 개수가 변경될 때만 호출

  const handleRemoveStock = (e, stockId) => {
    e.stopPropagation(); // 부모 클릭 이벤트 방지
    removeLikedStock(stockId);
  };
  
  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <div className={styles.headerContent}>
          <Heart size={24} className={styles.heartIcon} />
          <h1>관심 종목</h1>
        </div>
        <span className={styles.count}>{likedStocks.length}개</span>
      </header>
      
      <main className={styles.main}>
        {likedStocks.length === 0 ? (
          <motion.div 
            className={styles.emptyState}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className={styles.emptyIcon}>💝</div>
            <h2>관심 종목이 없습니다</h2>
            <p>홈에서 종목을 오른쪽으로 스와이프하면<br />관심 종목에 추가됩니다</p>
            <button 
              className={styles.goHomeButton}
              onClick={() => navigate('/home')}
            >
              종목 둘러보기
            </button>
          </motion.div>
        ) : (
          <>
            {/* 포트폴리오 요약 - 최상단 */}
            {portfolioSummary && (
              <motion.section 
                className={styles.portfolioSection}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0 }}
              >
                <div className={styles.portfolioHeader}>
                  <PieChart size={20} className={styles.sectionIcon} />
                  <h3>포트폴리오 요약</h3>
                </div>

                {/* AI 조언 */}
                <div className={styles.aiAdviceSection}>
                  <div className={styles.aiAdviceHeader}>
                    <Sparkles size={16} className={styles.aiIcon} />
                    <span className={styles.aiAdviceTitle}>AI 포트폴리오 분석</span>
                  </div>
                  {isLoadingAdvice ? (
                    <div className={styles.aiAdviceLoading}>
                      <Loader2 size={16} className={styles.loader} />
                      <span>AI가 포트폴리오를 분석 중입니다...</span>
                    </div>
                  ) : aiAdvice ? (
                    <div className={styles.aiAdviceText}>{aiAdvice}</div>
                  ) : (
                    <div className={styles.aiAdviceText}>분석 중...</div>
                  )}
                </div>

                {/* 주요 통계 */}
                <div className={styles.summaryCards}>
                  <div className={styles.summaryCard}>
                    <div className={styles.summaryCardHeader}>
                      <span className={styles.summaryCardLabel}>총 수익률</span>
                      <span className={`${styles.summaryCardValue} ${portfolioSummary.totalReturnPercent >= 0 ? styles.up : styles.down}`}>
                        {portfolioSummary.totalReturnPercent >= 0 ? '+' : ''}
                        {portfolioSummary.totalReturnPercent.toFixed(2)}%
                      </span>
                    </div>
                    <div className={styles.summaryCardSubtext}>
                      평균: {portfolioSummary.avgReturn >= 0 ? '+' : ''}{portfolioSummary.avgReturn.toFixed(2)}%
                    </div>
                  </div>

                  <div className={styles.summaryCard}>
                    <div className={styles.summaryCardHeader}>
                      <span className={styles.summaryCardLabel}>포트폴리오 가치</span>
                      <span className={styles.summaryCardValue}>
                        ₩{formatNumber(portfolioSummary.totalValue)}
                      </span>
                    </div>
                    <div className={styles.summaryCardSubtext}>
                      {portfolioSummary.totalGain >= 0 ? '+' : ''}₩{formatNumber(Math.abs(portfolioSummary.totalGain))}
                    </div>
                  </div>
                </div>

                {/* 섹터별 분포 */}
                <div className={styles.sectorDistribution}>
                  <div className={styles.sectionSubtitle}>
                    <BarChart3 size={16} />
                    <span>섹터별 분포</span>
                  </div>
                  <div className={styles.sectorList}>
                    {portfolioSummary.sectorDistribution.map((sector, index) => (
                      <div key={sector.category} className={styles.sectorItem}>
                        <div className={styles.sectorInfo}>
                          <span className={styles.sectorName}>{sector.category}</span>
                          <span className={styles.sectorCount}>{sector.count}개</span>
                        </div>
                        <div className={styles.sectorBar}>
                          <motion.div
                            className={styles.sectorBarFill}
                            initial={{ width: 0 }}
                            animate={{ width: `${sector.percentage}%` }}
                            transition={{ delay: 0.1 + index * 0.1, duration: 0.5 }}
                            style={{
                              background: `linear-gradient(90deg, #C64A3A ${sector.percentage}%, rgba(198, 74, 58, 0.2) 100%)`
                            }}
                          />
                        </div>
                        <span className={styles.sectorPercentage}>{sector.percentage.toFixed(0)}%</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* 최고/최저 수익 종목 */}
                <div className={styles.bestWorstSection}>
                  <div className={styles.bestWorstCard}>
                    <div className={styles.bestWorstHeader}>
                      <Award size={16} className={styles.up} />
                      <span className={styles.bestWorstLabel}>최고 수익</span>
                    </div>
                    <div 
                      className={styles.bestWorstStock}
                      onClick={() => navigate(`/stock/${portfolioSummary.bestStock.stock.id}`)}
                    >
                      <div className={styles.bestWorstStockInfo}>
                        <span className={styles.bestWorstStockName}>{portfolioSummary.bestStock.stock.name}</span>
                        <span className={styles.bestWorstStockTicker}>{portfolioSummary.bestStock.stock.ticker}</span>
                      </div>
                      <span className={`${styles.bestWorstReturn} ${styles.up}`}>
                        +{portfolioSummary.bestStock.returnPercent.toFixed(2)}%
                      </span>
                    </div>
                  </div>

                  <div className={styles.bestWorstCard}>
                    <div className={styles.bestWorstHeader}>
                      <TrendingDownIcon size={16} className={styles.down} />
                      <span className={styles.bestWorstLabel}>최저 수익</span>
                    </div>
                    <div 
                      className={styles.bestWorstStock}
                      onClick={() => navigate(`/stock/${portfolioSummary.worstStock.stock.id}`)}
                    >
                      <div className={styles.bestWorstStockInfo}>
                        <span className={styles.bestWorstStockName}>{portfolioSummary.worstStock.stock.name}</span>
                        <span className={styles.bestWorstStockTicker}>{portfolioSummary.worstStock.stock.ticker}</span>
                      </div>
                      <span className={`${styles.bestWorstReturn} ${styles.down}`}>
                        {portfolioSummary.worstStock.returnPercent.toFixed(2)}%
                      </span>
                    </div>
                  </div>
                </div>

                {/* 상세 통계 */}
                <div className={styles.detailedStats}>
                  <div className={styles.detailedStatsGrid}>
                    <div className={styles.detailedStatItem}>
                      <span className={styles.detailedStatValue}>{portfolioSummary.totalStocks}</span>
                      <span className={styles.detailedStatLabel}>관심 종목</span>
                    </div>
                    <div className={styles.detailedStatItem}>
                      <span className={styles.detailedStatValue}>{portfolioSummary.totalSectors}</span>
                      <span className={styles.detailedStatLabel}>섹터</span>
                    </div>
                    <div className={styles.detailedStatItem}>
                      <span className={`${styles.detailedStatValue} ${styles.up}`}>
                        {portfolioSummary.upStocks}
                      </span>
                      <span className={styles.detailedStatLabel}>상승</span>
                    </div>
                    <div className={styles.detailedStatItem}>
                      <span className={`${styles.detailedStatValue} ${styles.down}`}>
                        {portfolioSummary.downStocks}
                      </span>
                      <span className={styles.detailedStatLabel}>하락</span>
                    </div>
                  </div>
                </div>

                {/* 주요 키워드 */}
                {portfolioSummary.topKeywords.length > 0 && (
                  <div className={styles.topKeywordsSection}>
                    <div className={styles.sectionSubtitle}>
                      <span>주요 관심 키워드</span>
                    </div>
                    <div className={styles.topKeywordsList}>
                      {portfolioSummary.topKeywords.map((item, index) => (
                        <div key={item.keyword} className={styles.topKeywordItem}>
                          <span className={styles.topKeywordRank}>#{index + 1}</span>
                          <span className={styles.topKeywordName}>{item.keyword}</span>
                          <span className={styles.topKeywordCount}>{item.count}회</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </motion.section>
            )}

            {/* Keywords Summary */}
            {allKeywords.length > 0 && (
              <motion.section 
                className={styles.keywordsSection}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
              >
                <h3>관심 키워드</h3>
                <div className={styles.keywords}>
                  {allKeywords.map((keyword) => (
                    <span key={keyword} className={styles.keyword}>
                      {keyword}
                    </span>
                  ))}
                </div>
              </motion.section>
            )}
            
            {/* Stocks by Category */}
            {Object.entries(groupedStocks).map(([category, stocks], groupIndex) => (
              <motion.section 
                key={category}
                className={styles.categorySection}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: groupIndex * 0.1 }}
              >
                <h3 className={styles.categoryTitle}>{category}</h3>
                <div className={styles.stockList}>
                  {stocks.map((stock, index) => {
                    const priceChange = stock.currentPrice - stock.previousClose;
                    const priceChangePercent = (priceChange / stock.previousClose) * 100;
                    const isUp = priceChange >= 0;
                    
                    return (
                      <motion.div
                        key={stock.id}
                        className={styles.stockItem}
                        onClick={() => navigate(`/stock/${stock.id}`)}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: index * 0.05 }}
                        whileHover={{ backgroundColor: '#22252D' }}
                      >
                        <div className={styles.stockInfo}>
                          <div className={styles.stockName}>
                            <span className={styles.name}>{stock.name}</span>
                            <span className={styles.ticker}>{stock.ticker}</span>
                          </div>
                          <div className={styles.stockTags}>
                            {stock.keywords && stock.keywords.slice(0, 2).map((tag) => (
                              <span key={tag} className={styles.tag}>{tag}</span>
                            ))}
                          </div>
                        </div>
                        
                        <div className={styles.priceInfo}>
                          <span className={`${styles.price} mono`}>
                            ₩{formatPrice(stock.currentPrice)}
                          </span>
                          <span className={`${styles.change} ${isUp ? styles.up : styles.down}`}>
                            {isUp ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
                            <span className="mono">
                              {priceChangePercent >= 0 ? '+' : ''}{priceChangePercent.toFixed(2)}%
                            </span>
                          </span>
                        </div>
                        
                        <button
                          className={styles.removeButton}
                          onClick={(e) => handleRemoveStock(e, stock.id)}
                          aria-label="관심 종목에서 제거"
                        >
                          <X size={16} />
                        </button>
                        
                        <ChevronRight size={18} className={styles.chevron} />
                      </motion.div>
                    );
                  })}
                </div>
              </motion.section>
            ))}
            
          </>
        )}
      </main>
      
      <Navigation />
      <FloatingChatbot />
    </div>
  );
};

export default WatchList;


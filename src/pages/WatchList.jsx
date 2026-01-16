import { useNavigate, Navigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, Heart, ChevronRight } from 'lucide-react';
import { useApp } from '../context/AppContext';
import Navigation from '../components/Navigation';
import FloatingChatbot from '../components/FloatingChatbot';
import styles from './WatchList.module.css';

const WatchList = () => {
  const navigate = useNavigate();
  const { hasCompletedOnboarding, likedStocks } = useApp();
  
  if (!hasCompletedOnboarding) {
    return <Navigate to="/" replace />;
  }
  
  const formatPrice = (price) => price.toLocaleString('ko-KR');
  
  // Group stocks by category
  const groupedStocks = likedStocks.reduce((acc, stock) => {
    const category = stock.category[0];
    if (!acc[category]) {
      acc[category] = [];
    }
    acc[category].push(stock);
    return acc;
  }, {});
  
  // Extract all unique keywords from liked stocks
  const allKeywords = Array.from(
    new Set(likedStocks.flatMap(stock => stock.keywords))
  ).slice(0, 10);
  
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
            {/* Keywords Summary */}
            {allKeywords.length > 0 && (
              <motion.section 
                className={styles.keywordsSection}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
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
                            {stock.keywords.slice(0, 2).map((tag) => (
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
                        
                        <ChevronRight size={18} className={styles.chevron} />
                      </motion.div>
                    );
                  })}
                </div>
              </motion.section>
            ))}
            
            {/* Stats */}
            <motion.section 
              className={styles.statsSection}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
            >
              <h3>포트폴리오 요약</h3>
              <div className={styles.statsGrid}>
                <div className={styles.statItem}>
                  <span className={styles.statValue}>{likedStocks.length}</span>
                  <span className={styles.statLabel}>관심 종목</span>
                </div>
                <div className={styles.statItem}>
                  <span className={styles.statValue}>{Object.keys(groupedStocks).length}</span>
                  <span className={styles.statLabel}>섹터</span>
                </div>
                <div className={styles.statItem}>
                  <span className={`${styles.statValue} ${styles.up}`}>
                    {likedStocks.filter(s => s.currentPrice >= s.previousClose).length}
                  </span>
                  <span className={styles.statLabel}>상승 종목</span>
                </div>
                <div className={styles.statItem}>
                  <span className={`${styles.statValue} ${styles.down}`}>
                    {likedStocks.filter(s => s.currentPrice < s.previousClose).length}
                  </span>
                  <span className={styles.statLabel}>하락 종목</span>
                </div>
              </div>
            </motion.section>
          </>
        )}
      </main>
      
      <Navigation />
      <FloatingChatbot />
    </div>
  );
};

export default WatchList;


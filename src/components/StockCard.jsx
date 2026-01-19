import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, Volume2, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { useEffect, useState } from 'react';
import MiniChart from './MiniChart';
import styles from './StockCard.module.css';
import { stockAPI } from '../services/api';

const StockCard = ({
  stock,
  swipeDirection,
  swipeProgress,
  onClick,
  onNewsClick,
}) => {
  const [newsLoading, setNewsLoading] = useState(false);
  const [hasNews, setHasNews] = useState(stock.news && stock.news.length > 0);
  const [hasFetched, setHasFetched] = useState(false); // 크롤링 시도 여부
  
  // 뉴스 자동 크롤링 (한 번만 실행)
  useEffect(() => {
    const fetchNews = async () => {
      // 한 번도 시도하지 않았고, 뉴스가 없고, 종목 ID가 있으면 크롤링
      if (!hasFetched && !hasNews && stock.ticker) {
        try {
          setNewsLoading(true);
          setHasFetched(true); // 시도 플래그 설정
          console.log(`뉴스 크롤링 시작: ${stock.name} (${stock.ticker})`);
          
          await stockAPI.crawlStockNews(stock.ticker, 5);
          
          // 뉴스 크롤링 후 다시 종목 정보 가져오기
          const updatedStock = await stockAPI.getStock(stock.ticker);
          if (updatedStock.news && updatedStock.news.length > 0) {
            setHasNews(true);
            // 부모 컴포넌트에 업데이트된 뉴스 전달
            stock.news = updatedStock.news;
          }
        } catch (error) {
          console.error(`뉴스 크롤링 실패: ${stock.name}`, error);
        } finally {
          setNewsLoading(false);
        }
      }
    };
    
    fetchNews();
  }, [stock.ticker]); // 종목 ID만 의존성으로
  
  const priceChange = stock.currentPrice - stock.previousClose;
  const priceChangePercent = (priceChange / stock.previousClose) * 100;
  const isUp = priceChange >= 0;
  
  const formatPrice = (price) => {
    return price.toLocaleString('ko-KR');
  };
  
  const formatVolume = (volume) => {
    if (volume >= 1000000) {
      return `${(volume / 1000000).toFixed(1)}M`;
    }
    if (volume >= 1000) {
      return `${(volume / 1000).toFixed(0)}K`;
    }
    return volume.toString();
  };
  
  const likeGlow = swipeDirection === 'right' ? swipeProgress : 0;
  
  return (
    <motion.div
      className={styles.card}
      style={{
        boxShadow: likeGlow > 0
          ? `0 10px 28px rgba(0, 0, 0, 0.35), inset 0 0 0 ${2 + likeGlow}px rgba(198, 74, 58, ${likeGlow * 0.8})`
          : '0 10px 28px rgba(0, 0, 0, 0.35)',
        transform: `scale(${1 + likeGlow * 0.02})`,
        filter: swipeDirection === 'left' ? `saturate(${1 - swipeProgress * 0.5})` : 'none',
      }}
    >
      {/* Header */}
      <div className={styles.header} onClick={onClick}>
        <div className={styles.stockInfo}>
          <h2 className={styles.stockName}>{stock.name}</h2>
          <span className={styles.ticker}>{stock.ticker}</span>
        </div>
        <div className={`${styles.priceChange} ${isUp ? styles.up : styles.down}`}>
          {isUp ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
          <span className="mono">{priceChangePercent >= 0 ? '+' : ''}{priceChangePercent.toFixed(2)}%</span>
        </div>
      </div>
      
      {/* Chart */}
      <div className={styles.chartContainer} onClick={onClick}>
        <MiniChart data={stock.chartData} isUp={isUp} height={120} />
      </div>
      
      {/* Price Info */}
      <div className={styles.priceSection} onClick={onClick}>
        <div className={styles.currentPrice}>
          <span className={styles.priceLabel}>현재가</span>
          <span className={`${styles.priceValue} mono ${isUp ? styles.up : styles.down}`}>
            ₩{formatPrice(stock.currentPrice)}
          </span>
        </div>
      </div>
      
      {/* Metrics Grid */}
      <div className={styles.metricsGrid} onClick={onClick}>
        <div className={styles.metric}>
          <span className={styles.metricLabel}>고가</span>
          <span className={`${styles.metricValue} mono`}>₩{formatPrice(stock.high)}</span>
        </div>
        <div className={styles.metric}>
          <span className={styles.metricLabel}>저가</span>
          <span className={`${styles.metricValue} mono`}>₩{formatPrice(stock.low)}</span>
        </div>
        <div className={styles.metric}>
          <span className={styles.metricLabel}>
            <Volume2 size={12} style={{ marginRight: 4 }} />
            거래량
          </span>
          <span className={`${styles.metricValue} mono`}>{formatVolume(stock.volume)}</span>
        </div>
        <div className={styles.metric}>
          <span className={styles.metricLabel}>
            <ArrowUpRight size={12} className={styles.buyIcon} />
            매수
          </span>
          <span className={`${styles.metricValue} mono ${styles.buyText}`}>
            {formatVolume(stock.buyVolume)}
          </span>
        </div>
        <div className={styles.metric}>
          <span className={styles.metricLabel}>
            <ArrowDownRight size={12} className={styles.sellIcon} />
            매도
          </span>
          <span className={`${styles.metricValue} mono ${styles.sellText}`}>
            {formatVolume(stock.sellVolume)}
          </span>
        </div>
      </div>
      
      {/* News Section */}
      <div className={styles.newsSection}>
        <h4 className={styles.newsTitle}>연관 뉴스</h4>
        <div className={styles.newsList}>
          {stock.news && stock.news.length > 0 ? (
            stock.news.slice(0, 3).map((news, index) => (
              <div
                key={news.id}
                className={styles.newsItem}
                onClick={(e) => {
                  e.stopPropagation();
                  if (news.link) {
                    window.open(news.link, '_blank');
                  } else {
                    onNewsClick?.(news.id);
                  }
                }}
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                <span className={styles.newsDot}>•</span>
                <span className={styles.newsText}>{news.title}</span>
              </div>
            ))
          ) : (
            <>
              <div className={styles.newsItem} style={{ opacity: 0.3 }}>
                <span className={styles.newsDot}>•</span>
                <span className={styles.newsText}>
                  {newsLoading ? '뉴스 불러오는 중...' : '뉴스 준비 중...'}
                </span>
              </div>
              <div className={styles.newsItem} style={{ opacity: 0.2 }}>
                <span className={styles.newsDot}>•</span>
                <span className={styles.newsText}>뉴스 준비 중...</span>
              </div>
              <div className={styles.newsItem} style={{ opacity: 0.1 }}>
                <span className={styles.newsDot}>•</span>
                <span className={styles.newsText}>뉴스 준비 중...</span>
              </div>
            </>
          )}
        </div>
      </div>
      
      {/* Swipe Indicators */}
      {swipeDirection && (
        <motion.div
          className={`${styles.swipeIndicator} ${swipeDirection === 'right' ? styles.like : styles.pass}`}
          initial={{ opacity: 0 }}
          animate={{ opacity: swipeProgress }}
        >
          {swipeDirection === 'right' ? 'LIKE' : 'PASS'}
        </motion.div>
      )}
    </motion.div>
  );
};

export default StockCard;


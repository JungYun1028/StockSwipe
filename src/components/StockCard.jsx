import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, Volume2, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import MiniChart from './MiniChart';
import styles from './StockCard.module.css';

const StockCard = ({
  stock,
  swipeDirection,
  swipeProgress,
  onClick,
  onNewsClick,
}) => {
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
                  onNewsClick?.(news.id);
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
                <span className={styles.newsText}>뉴스 준비 중...</span>
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


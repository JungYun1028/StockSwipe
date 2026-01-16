import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown } from 'lucide-react';
import styles from './PreviewCards.module.css';

const PreviewCards = ({ stocks, onStockClick }) => {
  if (stocks.length === 0) return null;
  
  return (
    <div className={styles.container}>
      <h4 className={styles.title}>다음 추천 종목</h4>
      <div className={styles.cardsRow}>
        {stocks.slice(0, 2).map((stock, index) => {
          const priceChange = stock.currentPrice - stock.previousClose;
          const priceChangePercent = stock.previousClose > 0 ? (priceChange / stock.previousClose) * 100 : 0;
          const isUp = priceChange >= 0;
          
          return (
            <motion.div
              key={stock.id}
              className={styles.previewCard}
              onClick={() => onStockClick?.(stock.id)}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              whileHover={{ scale: 1.05, y: -5 }}
              whileTap={{ scale: 0.98 }}
            >
              <div className={styles.cardContent}>
                <span className={styles.stockName}>{stock.name}</span>
                <span className={styles.ticker}>{stock.ticker}</span>
                <div className={`${styles.change} ${isUp ? styles.up : styles.down}`}>
                  {isUp ? <TrendingUp size={10} /> : <TrendingDown size={10} />}
                  <span className="mono">{priceChangePercent >= 0 ? '+' : ''}{priceChangePercent.toFixed(1)}%</span>
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
};

export default PreviewCards;


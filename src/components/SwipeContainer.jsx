import { useState, useCallback } from 'react';
import { useSwipeable } from 'react-swipeable';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import StockCard from './StockCard';
import PreviewCards from './PreviewCards';
import styles from './SwipeContainer.module.css';

const SWIPE_THRESHOLD = 100;

const SwipeContainer = () => {
  const navigate = useNavigate();
  const { recommendedStocks, currentStockIndex, handleSwipe } = useApp();
  const [swipeDirection, setSwipeDirection] = useState(null);
  const [swipeProgress, setSwipeProgress] = useState(0);
  const [exitDirection, setExitDirection] = useState(null);
  
  const currentStock = recommendedStocks[currentStockIndex];
  const nextStocks = recommendedStocks.slice(currentStockIndex + 1, currentStockIndex + 6);
  
  const handlers = useSwipeable({
    onSwiping: (eventData) => {
      const progress = Math.min(Math.abs(eventData.deltaX) / SWIPE_THRESHOLD, 1);
      setSwipeProgress(progress);
      
      if (eventData.deltaX > 30) {
        setSwipeDirection('right');
      } else if (eventData.deltaX < -30) {
        setSwipeDirection('left');
      } else {
        setSwipeDirection(null);
      }
    },
    onSwipedLeft: (eventData) => {
      if (Math.abs(eventData.deltaX) >= SWIPE_THRESHOLD) {
        setExitDirection('left');
        setTimeout(() => {
          handleSwipe('left');
          setExitDirection(null);
        }, 300);
      }
      setSwipeDirection(null);
      setSwipeProgress(0);
    },
    onSwipedRight: (eventData) => {
      if (Math.abs(eventData.deltaX) >= SWIPE_THRESHOLD) {
        setExitDirection('right');
        setTimeout(() => {
          handleSwipe('right');
          setExitDirection(null);
        }, 300);
      }
      setSwipeDirection(null);
      setSwipeProgress(0);
    },
    onTouchEndOrOnMouseUp: () => {
      setSwipeDirection(null);
      setSwipeProgress(0);
    },
    trackMouse: true,
    preventScrollOnSwipe: true,
  });
  
  const handleCardClick = useCallback(() => {
    if (currentStock) {
      navigate(`/stock/${currentStock.id}`);
    }
  }, [currentStock, navigate]);
  
  const handleNewsClick = useCallback((newsId) => {
    if (currentStock) {
      navigate(`/news/${currentStock.id}/${newsId}`);
    }
  }, [currentStock, navigate]);
  
  const handlePreviewClick = useCallback((stockId) => {
    navigate(`/stock/${stockId}`);
  }, [navigate]);
  
  if (!currentStock) {
    return (
      <div className={styles.emptyState}>
        <div className={styles.emptyContent}>
          <span className={styles.emptyIcon}>🎉</span>
          <h2>모든 종목을 확인했어요!</h2>
          <p>관심 종목 목록에서 저장한 종목을 확인해보세요.</p>
        </div>
      </div>
    );
  }
  
  return (
    <div className={styles.container}>
      <div className={styles.swipeArea} {...handlers}>
        <AnimatePresence mode="wait">
          <motion.div
            key={currentStock.id}
            className={styles.cardWrapper}
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{
              scale: 1,
              opacity: 1,
              x: exitDirection === 'left' ? -500 : exitDirection === 'right' ? 500 : 0,
              rotate: exitDirection === 'left' ? -20 : exitDirection === 'right' ? 20 : 0,
            }}
            exit={{
              scale: 0.9,
              opacity: 0,
            }}
            transition={{
              type: 'spring',
              stiffness: 300,
              damping: 25,
            }}
          >
            <StockCard
              stock={currentStock}
              swipeDirection={swipeDirection}
              swipeProgress={swipeProgress}
              onClick={handleCardClick}
              onNewsClick={handleNewsClick}
            />
          </motion.div>
        </AnimatePresence>
        
        {/* Swipe hints */}
        <div className={styles.swipeHints}>
          <div className={`${styles.hint} ${styles.passHint} ${swipeDirection === 'left' ? styles.active : ''}`}>
            ← PASS
          </div>
          <div className={`${styles.hint} ${styles.likeHint} ${swipeDirection === 'right' ? styles.active : ''}`}>
            LIKE →
          </div>
        </div>
      </div>
      
      {/* Preview cards */}
      <PreviewCards stocks={nextStocks} onStockClick={handlePreviewClick} />
    </div>
  );
};

export default SwipeContainer;


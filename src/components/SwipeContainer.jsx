import { useState, useCallback } from 'react';
import { useSwipeable } from 'react-swipeable';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Heart, X, ArrowLeft, ArrowRight, Hand } from 'lucide-react';
import { useApp } from '../context/AppContext';
import StockCard from './StockCard';
import PreviewCards from './PreviewCards';
import styles from './SwipeContainer.module.css';

const SWIPE_THRESHOLD = 100;

const SwipeContainer = () => {
  const navigate = useNavigate();
  const { recommendedStocks, currentStockIndex, handleSwipe, hasSeenSwipeTutorial, completeSwipeTutorial, swipeHistory } = useApp();
  const [swipeDirection, setSwipeDirection] = useState(null);
  const [swipeProgress, setSwipeProgress] = useState(0);
  const [exitDirection, setExitDirection] = useState(null);
  const [showTutorial, setShowTutorial] = useState(!hasSeenSwipeTutorial);
  const isFirstTime = !hasSeenSwipeTutorial && swipeHistory.length === 0;
  
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

  const handleButtonSwipe = useCallback((direction) => {
    handleSwipe(direction);
  }, [handleSwipe]);

  const handleTutorialClose = useCallback(() => {
    setShowTutorial(false);
    completeSwipeTutorial();
  }, [completeSwipeTutorial]);
  
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
      {/* 튜토리얼 오버레이 */}
      {showTutorial && (
        <motion.div
          className={styles.tutorialOverlay}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <div className={styles.tutorialContent}>
            <div className={styles.tutorialHeader}>
              <Hand size={24} className={styles.tutorialIcon} />
              <h3>스와이프로 관심 종목을 선택하세요</h3>
            </div>
            <div className={styles.tutorialInstructions}>
              <div className={styles.tutorialStep}>
                <div className={styles.tutorialArrowRight}>
                  <ArrowRight size={32} />
                </div>
                <div className={styles.tutorialText}>
                  <strong>오른쪽으로 스와이프</strong>
                  <span>관심 종목에 추가</span>
                </div>
              </div>
              <div className={styles.tutorialStep}>
                <div className={styles.tutorialArrowLeft}>
                  <ArrowLeft size={32} />
                </div>
                <div className={styles.tutorialText}>
                  <strong>왼쪽으로 스와이프</strong>
                  <span>건너뛰기</span>
                </div>
              </div>
            </div>
            <button 
              className={styles.tutorialButton}
              onClick={handleTutorialClose}
            >
              시작하기
            </button>
          </div>
        </motion.div>
      )}

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
        
        {/* 개선된 Swipe hints */}
        <div className={styles.swipeHints}>
          <motion.div 
            className={`${styles.hint} ${styles.passHint} ${swipeDirection === 'left' ? styles.active : ''}`}
            animate={swipeDirection === 'left' ? { scale: 1.1 } : { scale: 1 }}
            transition={{ type: 'spring', stiffness: 300 }}
          >
            <X size={16} />
            <span>PASS</span>
            {isFirstTime && (
              <motion.div
                className={styles.hintArrow}
                animate={{ x: [-8, 8, -8] }}
                transition={{ repeat: Infinity, duration: 1.5 }}
              >
                ←
              </motion.div>
            )}
          </motion.div>
          
          <div className={styles.swipeHintCenter}>
            {isFirstTime && (
              <motion.div
                className={styles.swipeGuide}
                animate={{ 
                  opacity: [0.4, 1, 0.4],
                  scale: [1, 1.05, 1]
                }}
                transition={{ repeat: Infinity, duration: 2 }}
              >
                <Hand size={20} />
                <span>스와이프하세요</span>
              </motion.div>
            )}
          </div>
          
          <motion.div 
            className={`${styles.hint} ${styles.likeHint} ${swipeDirection === 'right' ? styles.active : ''}`}
            animate={swipeDirection === 'right' ? { scale: 1.1 } : { scale: 1 }}
            transition={{ type: 'spring', stiffness: 300 }}
          >
            <Heart size={16} fill={swipeDirection === 'right' ? 'currentColor' : 'none'} />
            <span>LIKE</span>
            {isFirstTime && (
              <motion.div
                className={styles.hintArrow}
                animate={{ x: [-8, 8, -8] }}
                transition={{ repeat: Infinity, duration: 1.5 }}
              >
                →
              </motion.div>
            )}
          </motion.div>
        </div>
      </div>

      {/* 액션 버튼 */}
      <div className={styles.actionButtons}>
        <motion.button
          className={`${styles.actionButton} ${styles.passButton}`}
          onClick={() => handleButtonSwipe('left')}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          aria-label="Pass"
        >
          <X size={24} />
          <span>Pass</span>
        </motion.button>
        
        <motion.button
          className={`${styles.actionButton} ${styles.likeButton}`}
          onClick={() => handleButtonSwipe('right')}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          aria-label="Like"
        >
          <Heart size={24} />
          <span>Like</span>
        </motion.button>
      </div>
      
      {/* Preview cards */}
      <PreviewCards stocks={nextStocks} onStockClick={handlePreviewClick} />
    </div>
  );
};

export default SwipeContainer;


import React, { createContext, useContext, useState, useCallback, useEffect, useMemo } from 'react';
import { stockAPI } from '../services/api';
import { mapStocksData } from '../utils/stockDataMapper';

const AppContext = createContext(undefined);

// Recommendation scoring function
const calculateScore = (
  stock,
  categoryWeights,
  keywordWeights,
  swipeHistory
) => {
  // Category weight (40%)
  const categoryScore = stock.category && stock.category.length > 0
    ? stock.category.reduce((sum, cat) => sum + (categoryWeights[cat] || 0), 0) / stock.category.length
    : 0;
  
  // Keyword weight (20%)
  const keywordScore = stock.keywords && stock.keywords.length > 0
    ? stock.keywords.reduce((sum, keyword) => sum + (keywordWeights[keyword] || 0), 0) / stock.keywords.length
    : 0;
  
  // Swipe history weight (30%)
  const recentSwipes = swipeHistory.filter(s => s.stockId === stock.id);
  const swipeScore = recentSwipes.length > 0
    ? recentSwipes.filter(s => s.action === 'like').length / recentSwipes.length
    : 0.5;
  
  // Random factor (10%) - to add variety
  const randomFactor = Math.random();
  
  return (
    categoryScore * 0.4 +
    keywordScore * 0.2 +
    swipeScore * 0.3 +
    randomFactor * 0.1
  );
};

export const AppProvider = ({ children }) => {
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [hasCompletedOnboarding, setHasCompletedOnboarding] = useState(false);
  const [swipeHistory, setSwipeHistory] = useState([]);
  const [likedStocks, setLikedStocks] = useState([]);
  const [currentStockIndex, setCurrentStockIndex] = useState(0);
  const [chatMessages, setChatMessages] = useState([]);
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [chatContext, setChatContext] = useState(null);
  const [allStocks, setAllStocks] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  
  // Initialize weights based on selected categories
  const [categoryWeights, setCategoryWeights] = useState({});
  const [keywordWeights, setKeywordWeights] = useState({});
  
  // Fetch stocks from API
  useEffect(() => {
    const fetchStocks = async () => {
      try {
        setIsLoading(true);
        const backendStocks = await stockAPI.getAllStocks();
        console.log('Backend stocks:', backendStocks.length);
        console.log('First stock:', backendStocks[0]);
        
        const mappedStocks = mapStocksData(backendStocks);
        console.log('Mapped stocks:', mappedStocks.length);
        console.log('First mapped stock:', mappedStocks[0]);
        
        setAllStocks(mappedStocks);
      } catch (error) {
        console.error('Failed to fetch stocks:', error);
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchStocks();
  }, []);
  
  const completeOnboarding = useCallback(() => {
    // Set initial weights based on selected categories
    const initialCategoryWeights = {};
    const categoryNameMap = {
      bio: ['바이오', '제약'],
      ai: ['AI', 'IT'],
      ship: ['선박', '조선'],
      food: ['식품', '소비재'],
      energy: ['에너지', '태양광'],
      semi: ['반도체'],
      finance: ['금융', '은행', '배당주'],
      battery: ['2차전지'],
    };
    
    selectedCategories.forEach(catId => {
      const names = categoryNameMap[catId] || [];
      names.forEach(name => {
        initialCategoryWeights[name] = 1;
      });
    });
    
    setCategoryWeights(initialCategoryWeights);
    
    // 카테고리 변경 시 스와이프 히스토리 초기화
    setSwipeHistory([]);
    setCurrentStockIndex(0);
    
    setHasCompletedOnboarding(true);
  }, [selectedCategories]);
  
  // Get recommended stocks based on weights
  const recommendedStocks = useMemo(() => {
    console.log('=== Filtering stocks ===');
    console.log('selectedCategories:', selectedCategories);
    console.log('allStocks count:', allStocks.length);
    
    let stocks = selectedCategories.length > 0
      ? allStocks.filter(stock => {
          // categoryCode로 필터링 (백엔드에서 가져온 카테고리 코드)
          const match = selectedCategories.includes(stock.categoryCode);
          if (allStocks.indexOf(stock) < 3) {
            console.log(`Stock ${stock.name}: categoryCode=${stock.categoryCode}, match=${match}`);
          }
          return match;
        })
      : allStocks;
    
    console.log('After category filter:', stocks.length);
    
    // Filter out already swiped stocks
    const swipedIds = new Set(swipeHistory.map(s => s.stockId));
    stocks = stocks.filter(s => !swipedIds.has(s.id));
    
    console.log('After swipe filter:', stocks.length);
    
    // Sort by score
    const sorted = stocks.sort((a, b) => {
      const scoreA = calculateScore(a, categoryWeights, keywordWeights, swipeHistory);
      const scoreB = calculateScore(b, categoryWeights, keywordWeights, swipeHistory);
      return scoreB - scoreA;
    });
    
    console.log('Recommended stocks:', sorted.length);
    return sorted;
  }, [selectedCategories, categoryWeights, keywordWeights, swipeHistory, allStocks]);
  
  const getCurrentStock = useCallback(() => {
    return recommendedStocks[currentStockIndex] || null;
  }, [recommendedStocks, currentStockIndex]);
  
  const handleSwipe = useCallback((direction) => {
    const currentStock = recommendedStocks[currentStockIndex];
    if (!currentStock) return;
    
    const action = direction === 'right' ? 'like' : 'pass';
    
    // Add to history
    setSwipeHistory(prev => [...prev, {
      stockId: currentStock.id,
      action,
      timestamp: new Date(),
    }]);
    
    if (action === 'like') {
      // Add to liked stocks
      setLikedStocks(prev => [...prev, currentStock]);
      
      // Increase weights for this stock's categories and keywords
      setCategoryWeights(prev => {
        const updated = { ...prev };
        currentStock.category.forEach(cat => {
          updated[cat] = (updated[cat] || 0) + 0.2;
        });
        return updated;
      });
      
      setKeywordWeights(prev => {
        const updated = { ...prev };
        currentStock.keywords.forEach(keyword => {
          updated[keyword] = (updated[keyword] || 0) + 0.15;
        });
        return updated;
      });
    } else {
      // Decrease weights for this stock's categories and keywords
      setCategoryWeights(prev => {
        const updated = { ...prev };
        currentStock.category.forEach(cat => {
          updated[cat] = Math.max((updated[cat] || 0) - 0.1, 0);
        });
        return updated;
      });
      
      setKeywordWeights(prev => {
        const updated = { ...prev };
        currentStock.keywords.forEach(keyword => {
          updated[keyword] = Math.max((updated[keyword] || 0) - 0.05, 0);
        });
        return updated;
      });
    }
    
    // Move to next stock
    setCurrentStockIndex(prev => prev + 1);
  }, [recommendedStocks, currentStockIndex]);
  
  const addChatMessage = useCallback((role, content) => {
    setChatMessages(prev => [...prev, {
      role,
      content,
      timestamp: new Date(),
    }]);
  }, []);
  
  const clearChat = useCallback(() => {
    setChatMessages([]);
  }, []);
  
  return (
    <AppContext.Provider value={{
      selectedCategories,
      setSelectedCategories,
      hasCompletedOnboarding,
      completeOnboarding,
      recommendedStocks,
      currentStockIndex,
      getCurrentStock,
      swipeHistory,
      handleSwipe,
      likedStocks,
      chatMessages,
      addChatMessage,
      clearChat,
      isChatOpen,
      setIsChatOpen,
      chatContext,
      setChatContext,
      categoryWeights,
      keywordWeights,
      isLoading,
      allStocks,
    }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};


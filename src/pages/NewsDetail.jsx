import { useEffect, useState } from 'react';
import { useParams, useNavigate, Navigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowLeft, Sparkles, ExternalLink } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { stockAPI } from '../services/api';
import FloatingChatbot from '../components/FloatingChatbot';
import styles from './NewsDetail.module.css';

// Mock full news content
const getNewsContent = (newsId, stock) => {
  const news = stock.news.find(n => n.id === newsId);
  if (!news) {
    return {
      headline: '뉴스를 찾을 수 없습니다',
      aiSummary: '',
      body: '',
      keywords: [],
    };
  }
  
  const bodyTemplates = [
    `${stock.name}이(가) 새로운 전환점을 맞이하고 있다. 업계 관계자들은 이번 소식이 ${stock.category[0]} 섹터 전반에 긍정적인 영향을 미칠 것으로 전망한다.\n\n${news.summary}\n\n전문가들은 "${stock.name}의 이번 행보가 시장에서 주목받고 있다"며 "향후 실적 개선에 기여할 것으로 보인다"고 분석했다.\n\n한편, ${stock.keywords.join(', ')} 등 관련 키워드가 투자자들 사이에서 화두가 되고 있다. 시장에서는 이러한 움직임이 중장기적인 성장 동력으로 작용할 것이라는 기대가 커지고 있다.`,
    `${stock.category[0]} 섹터를 대표하는 ${stock.name}이(가) 다시 한번 시장의 이목을 집중시키고 있다.\n\n${news.summary}\n\n업계에서는 이번 뉴스가 ${stock.name}의 기업 가치 재평가로 이어질 수 있다고 분석한다. 특히 ${stock.keywords[0]}과 관련된 성장 가능성이 주목받고 있다.\n\n증권가에서는 "투자자들이 주목해야 할 시점"이라며 다양한 분석 의견을 내놓고 있다.`,
  ];
  
  return {
    headline: news.title,
    aiSummary: news.summary,
    body: bodyTemplates[parseInt(newsId.slice(-1)) % 2],
    keywords: [stock.name, ...stock.keywords.slice(0, 3), stock.category[0]],
  };
};

const NewsDetail = () => {
  const { stockId, newsId } = useParams();
  const navigate = useNavigate();
  const { hasCompletedOnboarding, setChatContext } = useApp();
  const [showKeywordSuggestion, setShowKeywordSuggestion] = useState(true);
  const [stock, setStock] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  
  useEffect(() => {
    const fetchStock = async () => {
      try {
        setIsLoading(true);
        const data = await stockAPI.getStockById(stockId);
        setStock(data);
      } catch (error) {
        console.error('Failed to fetch stock:', error);
      } finally {
        setIsLoading(false);
      }
    };
    
    if (stockId) {
      fetchStock();
    }
  }, [stockId]);
  
  const newsContent = stock && newsId ? getNewsContent(newsId, stock) : null;
  
  useEffect(() => {
    if (stock) {
      setChatContext({ type: 'news', data: stock });
    }
    return () => setChatContext(null);
  }, [stock, setChatContext]);
  
  if (!hasCompletedOnboarding) {
    return <Navigate to="/" replace />;
  }
  
  if (isLoading) {
    return (
      <div className={styles.container}>
        <div className={styles.loading}>로딩 중...</div>
      </div>
    );
  }
  
  if (!stock || !newsContent) {
    return (
      <div className={styles.container}>
        <div className={styles.error}>뉴스를 찾을 수 없습니다.</div>
      </div>
    );
  }
  
  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <button className={styles.backButton} onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <span className={styles.headerTitle}>뉴스 상세</span>
        <div style={{ width: 40 }} />
      </header>
      
      <main className={styles.main}>
        {/* Headline */}
        <motion.div
          className={styles.headline}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <span className={styles.stockBadge}>{stock.name}</span>
          <h1>{newsContent.headline}</h1>
        </motion.div>
        
        {/* AI Summary */}
        <motion.div
          className={styles.aiSummary}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <div className={styles.aiHeader}>
            <Sparkles size={16} className={styles.sparkle} />
            <span>AI 한 줄 요약</span>
          </div>
          <p>{newsContent.aiSummary}</p>
        </motion.div>
        
        {/* Keyword Suggestion */}
        {showKeywordSuggestion && (
          <motion.div
            className={styles.keywordSuggestion}
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.2 }}
          >
            <button 
              className={styles.closeButton}
              onClick={() => setShowKeywordSuggestion(false)}
            >
              ×
            </button>
            <div className={styles.suggestionContent}>
              <span className={styles.suggestionIcon}>💡</span>
              <div>
                <p className={styles.suggestionTitle}>이 뉴스에서 이런 키워드가 보여요</p>
                <div className={styles.suggestionKeywords}>
                  {newsContent.keywords.slice(0, 4).map((keyword) => (
                    <span key={keyword} className={styles.suggestionKeyword}>
                      {keyword}
                    </span>
                  ))}
                </div>
                <p className={styles.suggestionHint}>챗봇에게 물어보세요!</p>
              </div>
            </div>
          </motion.div>
        )}
        
        {/* News Body */}
        <motion.article
          className={styles.body}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          {newsContent.body.split('\n\n').map((paragraph, index) => (
            <p key={index}>{paragraph}</p>
          ))}
        </motion.article>
        
        {/* Related Stock */}
        <motion.div
          className={styles.relatedStock}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          onClick={() => navigate(`/stock/${stock.id}`)}
        >
          <div className={styles.relatedInfo}>
            <span className={styles.relatedLabel}>관련 종목</span>
            <span className={styles.relatedName}>{stock.name}</span>
            <span className={styles.relatedTicker}>{stock.ticker}</span>
          </div>
          <ExternalLink size={18} className={styles.linkIcon} />
        </motion.div>
        
        {/* Keywords */}
        <motion.div
          className={styles.keywords}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <h3>관련 키워드</h3>
          <div className={styles.keywordList}>
            {newsContent.keywords.map((keyword) => (
              <span key={keyword} className={styles.keyword}>
                #{keyword}
              </span>
            ))}
          </div>
        </motion.div>
      </main>
      
      <FloatingChatbot />
    </div>
  );
};

export default NewsDetail;


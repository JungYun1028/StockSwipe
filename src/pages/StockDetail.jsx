import { useEffect, useState } from 'react';
import { useParams, useNavigate, Navigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, 
  TrendingUp, 
  TrendingDown, 
  Heart,
  Activity,
  BarChart3,
  FileText,
  Target
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { stockAPI } from '../services/api';
import { mapStockData } from '../utils/stockDataMapper';
import MiniChart from '../components/MiniChart';
import FloatingChatbot from '../components/FloatingChatbot';
import styles from './StockDetail.module.css';

const StockDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasCompletedOnboarding, likedStocks, setChatContext } = useApp();
  const [stock, setStock] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  
  useEffect(() => {
    const fetchStock = async () => {
      try {
        setIsLoading(true);
        const backendData = await stockAPI.getStockById(id);
        const mappedStock = mapStockData(backendData);
        setStock(mappedStock);

        // 뉴스가 없으면 자동으로 가져오기
        if (!mappedStock.news || mappedStock.news.length === 0) {
          console.log('📰 뉴스가 없어서 자동으로 가져옵니다...');
          try {
            await stockAPI.fetchNewsForStock(id);
            // 뉴스 가져온 후 다시 종목 정보 로드
            const updatedData = await stockAPI.getStockById(id);
            const updatedStock = mapStockData(updatedData);
            setStock(updatedStock);
          } catch (newsError) {
            console.error('뉴스 가져오기 실패:', newsError);
          }
        }
      } catch (error) {
        console.error('Failed to fetch stock:', error);
      } finally {
        setIsLoading(false);
      }
    };
    
    if (id) {
      fetchStock();
    }
  }, [id]);
  
  const isLiked = likedStocks.some(s => s.id === id);
  
  useEffect(() => {
    if (stock) {
      setChatContext({ type: 'stock', data: stock });
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
  
  if (!stock) {
    return (
      <div className={styles.container}>
        <div className={styles.error}>종목을 찾을 수 없습니다.</div>
      </div>
    );
  }
  
  const priceChange = stock.currentPrice - stock.previousClose;
  const priceChangePercent = stock.previousClose > 0 ? (priceChange / stock.previousClose) * 100 : 0;
  const isUp = priceChange >= 0;
  
  const formatPrice = (price) => price?.toLocaleString('ko-KR') || '0';
  const formatVolume = (volume) => {
    if (!volume) return '0';
    if (volume >= 1000000) return `${(volume / 1000000).toFixed(1)}M`;
    if (volume >= 1000) return `${(volume / 1000).toFixed(0)}K`;
    return volume.toString();
  };
  
  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <button className={styles.backButton} onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <div className={styles.headerTitle}>
          <h1>{stock.name}</h1>
          <span className={styles.ticker}>{stock.ticker}</span>
        </div>
        <div className={`${styles.likeIndicator} ${isLiked ? styles.liked : ''}`}>
          <Heart size={20} fill={isLiked ? '#C64A3A' : 'none'} />
        </div>
      </header>
      
      <main className={styles.main}>
        {/* Price Section */}
        <motion.section 
          className={styles.priceSection}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className={styles.currentPrice}>
            <span className={`${styles.price} mono ${isUp ? styles.up : styles.down}`}>
              ₩{formatPrice(stock.currentPrice)}
            </span>
            <div className={`${styles.change} ${isUp ? styles.up : styles.down}`}>
              {isUp ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
              <span className="mono">
                {priceChange >= 0 ? '+' : ''}{formatPrice(Math.abs(priceChange))} ({priceChangePercent >= 0 ? '+' : ''}{priceChangePercent.toFixed(2)}%)
              </span>
            </div>
          </div>
          
          <div className={styles.chartWrapper}>
            <MiniChart data={stock.chartData} isUp={isUp} height={180} />
          </div>
          
          {/* Price Grid */}
          <div className={styles.priceGrid}>
            <div className={styles.priceItem}>
              <span className={styles.priceLabel}>고가</span>
              <span className={`${styles.priceValue} mono`}>₩{formatPrice(stock.high)}</span>
            </div>
            <div className={styles.priceItem}>
              <span className={styles.priceLabel}>저가</span>
              <span className={`${styles.priceValue} mono`}>₩{formatPrice(stock.low)}</span>
            </div>
            <div className={styles.priceItem}>
              <span className={styles.priceLabel}>거래량</span>
              <span className={`${styles.priceValue} mono`}>{formatVolume(stock.volume)}</span>
            </div>
            <div className={styles.priceItem}>
              <span className={styles.priceLabel}>
                <span style={{ color: '#C64A3A' }}>↗</span> 매수
              </span>
              <span className={`${styles.priceValue} mono`} style={{ color: '#C64A3A' }}>
                {formatVolume(stock.buyVolume)}
              </span>
            </div>
            <div className={styles.priceItem}>
              <span className={styles.priceLabel}>
                <span style={{ color: '#4A6FA5' }}>↘</span> 매도
              </span>
              <span className={`${styles.priceValue} mono`} style={{ color: '#4A6FA5' }}>
                {formatVolume(stock.sellVolume)}
              </span>
            </div>
          </div>
        </motion.section>
        
        {/* Company Overview */}
        <motion.section 
          className={styles.section}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <h2 className={styles.sectionTitle}>
            <FileText size={18} />
            기업 개요
          </h2>
          <div className={styles.card}>
            <p className={styles.description}>
              {stock.description || '국내 최대 인터넷 플랫폼을 운영하며 AI 선도 기업으로 자리잡고 있습니다.'}
            </p>
            <div className={styles.divider} />
            <h4 className={styles.subTitle}>사업 내용</h4>
            <p className={styles.businessText}>
              {stock.business || '검색, 커머스, 콘텐츠, 클라우드 등 다양한 서비스를 운영'}
            </p>
          </div>
        </motion.section>
        
        {/* Technical Indicators */}
        <motion.section 
          className={styles.section}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <h2 className={styles.sectionTitle}>
            <Activity size={18} />
            기술적 지표
          </h2>
          <div className={styles.card}>
            <div className={styles.indicatorGrid}>
              <div className={styles.indicator}>
                <span className={styles.indicatorLabel}>RSI</span>
                <div className={styles.indicatorValue}>
                  <span className={`mono`}>45</span>
                  <span className={styles.indicatorHint}>중립</span>
                </div>
              </div>
            </div>
            
            <div className={styles.divider} />
            
            <h4 className={styles.subTitle}>이동평균선</h4>
            <div className={styles.maGrid}>
              <div className={styles.maItem}>
                <span className={styles.maLabel}>MA20</span>
                <span className={`${styles.maValue} mono`}>
                  ₩{formatPrice(Math.floor(stock.currentPrice * 0.98))}
                </span>
              </div>
              <div className={styles.maItem}>
                <span className={styles.maLabel}>MA60</span>
                <span className={`${styles.maValue} mono`}>
                  ₩{formatPrice(Math.floor(stock.currentPrice * 0.95))}
                </span>
              </div>
              <div className={styles.maItem}>
                <span className={styles.maLabel}>MA120</span>
                <span className={`${styles.maValue} mono`}>
                  ₩{formatPrice(Math.floor(stock.currentPrice * 0.93))}
                </span>
              </div>
            </div>
          </div>
        </motion.section>
        
        {/* Analyst Rating */}
        <motion.section 
          className={styles.section}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <h2 className={styles.sectionTitle}>
            <Target size={18} />
            전문가 분석
          </h2>
          <div className={styles.card}>
            <div className={styles.ratingSection}>
              <div className={`${styles.ratingBadge} ${styles.ratingHold}`}>
                중립
              </div>
              <p className={styles.ratingReason}>AI 의견</p>
            </div>
          </div>
        </motion.section>
        
        {/* Related News */}
        <motion.section 
          className={styles.section}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <h2 className={styles.sectionTitle}>
            <BarChart3 size={18} />
            관련 뉴스
          </h2>
          <div className={styles.newsList}>
            {stock.news && stock.news.length > 0 ? (
              stock.news.map((news) => {
                console.log('📰 뉴스 데이터:', {
                  title: news.title,
                  link: news.link,
                  hasLink: !!news.link
                });
                
                return (
                  <div 
                    key={news.id} 
                    className={styles.newsCard}
                    onClick={() => {
                      console.log('🖱️ 뉴스 클릭:', news.link);
                      if (news.link) {
                        window.open(news.link, '_blank');
                      } else {
                        console.error('❌ 뉴스 링크가 없습니다:', news);
                      }
                    }}
                    style={{ cursor: news.link ? 'pointer' : 'default' }}
                  >
                    <div className={styles.newsTitleRow}>
                      {news.sentiment && (
                        <span className={`${styles.sentimentBadge} ${styles[news.sentiment.toLowerCase()]}`}>
                          {news.sentiment === 'POSITIVE' ? '📈 호재' : news.sentiment === 'NEGATIVE' ? '📉 악재' : '━ 중립'}
                        </span>
                      )}
                      <h4 className={styles.newsTitle}>{news.title}</h4>
                    </div>
                    <p className={styles.newsSummary}>{news.summary}</p>
                    {news.source && (
                      <span className={styles.newsSource}>{news.source}</span>
                    )}
                    {!news.link && (
                      <span className={styles.newsError}>⚠️ 링크 없음</span>
                    )}
                  </div>
                );
              })
            ) : (
              <>
                <div className={styles.newsCard}>
                  <h4 className={styles.newsTitle}>기사 제목</h4>
                  <p className={styles.newsSummary}>기사 내용 요약</p>
                </div>
                <div className={styles.newsCard}>
                  <h4 className={styles.newsTitle}>기사 제목</h4>
                  <p className={styles.newsSummary}>기사 내용 요약</p>
                </div>
                <div className={styles.newsCard}>
                  <h4 className={styles.newsTitle}>기사 제목</h4>
                  <p className={styles.newsSummary}>기사 내용 요약</p>
                </div>
              </>
            )}
          </div>
        </motion.section>
        
        {/* Keywords */}
        <motion.section 
          className={styles.section}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <h2 className={styles.sectionTitle}>키워드</h2>
          <div className={styles.keywords}>
            {stock.keywords && stock.keywords.length > 0 ? (
              stock.keywords.map((keyword) => (
                <span key={keyword} className={styles.keyword}>
                  {keyword}
                </span>
              ))
            ) : (
              <>
                <span className={styles.keyword}>검색</span>
                <span className={styles.keyword}>커머스</span>
                <span className={styles.keyword}>콘텐츠</span>
                <span className={styles.keyword}>클라우드</span>
                <span className={styles.keyword}>AI</span>
              </>
            )}
          </div>
        </motion.section>
      </main>
      
      <FloatingChatbot />
    </div>
  );
};

export default StockDetail;

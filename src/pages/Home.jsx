import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import SwipeContainer from '../components/SwipeContainer';
import Navigation from '../components/Navigation';
import FloatingChatbot from '../components/FloatingChatbot';
import styles from './Home.module.css';

const Home = () => {
  const { hasCompletedOnboarding, getCurrentStock, setChatContext, isLoading, recommendedStocks } = useApp();
  
  // Redirect to category selection if not onboarded
  if (!hasCompletedOnboarding) {
    return <Navigate to="/" replace />;
  }
  
  console.log('Home rendered - isLoading:', isLoading, 'recommendedStocks:', recommendedStocks?.length);
  
  const currentStock = getCurrentStock();
  
  // Set chat context to current stock
  useEffect(() => {
    if (currentStock) {
      setChatContext({ type: 'stock', data: currentStock });
    }
  }, [currentStock, setChatContext]);
  
  // 로딩 중일 때
  if (isLoading) {
    return (
      <div className={styles.container}>
        <header className={styles.header}>
          <div className={styles.logo}>
            <span className={styles.logoIcon}>📈</span>
            <span className={styles.logoText}>StockSwipe</span>
          </div>
        </header>
        <main className={styles.main}>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            height: '100%',
            flexDirection: 'column',
            gap: '1rem'
          }}>
            <div style={{ fontSize: '3rem' }}>📊</div>
            <div style={{ color: '#888' }}>종목 데이터를 불러오는 중...</div>
          </div>
        </main>
        <Navigation />
      </div>
    );
  }
  
  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <div className={styles.logo}>
          <span className={styles.logoIcon}>📈</span>
          <span className={styles.logoText}>StockSwipe</span>
        </div>
      </header>
      
      <main className={styles.main}>
        <SwipeContainer />
      </main>
      
      <Navigation />
      <FloatingChatbot />
    </div>
  );
};

export default Home;


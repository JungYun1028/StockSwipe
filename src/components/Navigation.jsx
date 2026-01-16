import { useNavigate, useLocation } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Home, Heart, LayoutGrid } from 'lucide-react';
import { useApp } from '../context/AppContext';
import styles from './Navigation.module.css';

const Navigation = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { likedStocks } = useApp();
  
  const navItems = [
    { id: 'home', icon: Home, label: '홈', path: '/home' },
    { id: 'watchlist', icon: Heart, label: '관심종목', path: '/watchlist', badge: likedStocks.length },
    { id: 'categories', icon: LayoutGrid, label: '카테고리', path: '/' },
  ];
  
  return (
    <nav className={styles.nav}>
      <div className={styles.container}>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          const Icon = item.icon;
          
          return (
            <button
              key={item.id}
              className={`${styles.navItem} ${isActive ? styles.active : ''}`}
              onClick={() => navigate(item.path)}
            >
              <div className={styles.iconWrapper}>
                <Icon size={22} strokeWidth={isActive ? 2.5 : 2} />
                {item.badge !== undefined && item.badge > 0 && (
                  <motion.span 
                    className={styles.badge}
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: 'spring', stiffness: 500 }}
                  >
                    {item.badge > 99 ? '99+' : item.badge}
                  </motion.span>
                )}
              </div>
              <span className={styles.label}>{item.label}</span>
              {isActive && (
                <motion.div
                  className={styles.activeIndicator}
                  layoutId="navIndicator"
                  transition={{ type: 'spring', stiffness: 500, damping: 30 }}
                />
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};

export default Navigation;


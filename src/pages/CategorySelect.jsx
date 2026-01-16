import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { stockAPI } from '../services/api';
import styles from './CategorySelect.module.css';

const CategorySelect = () => {
  const navigate = useNavigate();
  const { selectedCategories, setSelectedCategories, completeOnboarding, isLoading, allStocks } = useApp();
  const [categories, setCategories] = useState([]);
  
  // 디버깅
  useEffect(() => {
    console.log('CategorySelect - allStocks:', allStocks?.length, 'isLoading:', isLoading);
  }, [allStocks, isLoading]);
  
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const cats = await stockAPI.getCategories();
        
        // 백엔드 카테고리에 아이콘 매핑
        const iconMap = {
          bio: '🧬',
          ai: '🤖',
          ship: '🚢',
          food: '🍜',
          energy: '⚡',
          semi: '💾',
          finance: '🏦',
          battery: '🔋',
        };
        
        const categoriesWithIcons = cats.map(cat => ({
          id: cat.code,
          name: cat.name,
          icon: iconMap[cat.code] || '📈',
        }));
        
        setCategories(categoriesWithIcons);
      } catch (error) {
        console.error('Failed to fetch categories:', error);
        // Fallback to default categories
        setCategories([
          { id: 'bio', name: '바이오', icon: '🧬' },
          { id: 'ai', name: 'AI', icon: '🤖' },
          { id: 'ship', name: '선박', icon: '🚢' },
          { id: 'food', name: '식품', icon: '🍜' },
          { id: 'energy', name: '에너지', icon: '⚡' },
          { id: 'semi', name: '반도체', icon: '💾' },
          { id: 'finance', name: '금융', icon: '🏦' },
          { id: 'battery', name: '2차전지', icon: '🔋' },
        ]);
      }
    };
    
    fetchCategories();
  }, []);
  
  const toggleCategory = (categoryId) => {
    setSelectedCategories(
      selectedCategories.includes(categoryId)
        ? selectedCategories.filter(id => id !== categoryId)
        : [...selectedCategories, categoryId]
    );
  };
  
  const handleStart = () => {
    if (selectedCategories.length > 0) {
      console.log('=== Starting with categories ===');
      console.log('Selected:', selectedCategories);
      completeOnboarding();
      console.log('Onboarding completed, navigating to /home');
      navigate('/home');
    }
  };
  
  return (
    <div className={styles.container}>
      <motion.div 
        className={styles.content}
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <div className={styles.header}>
          <h1 className={styles.title}>관심 있는 섹터를<br />선택해주세요</h1>
          <p className={styles.subtitle}>선택한 카테고리를 기반으로 종목을 추천해드려요</p>
        </div>
        
        <div className={styles.categoriesGrid}>
          {categories.map((category, index) => {
            const isSelected = selectedCategories.includes(category.id);
            return (
              <motion.button
                key={category.id}
                className={`${styles.categoryButton} ${isSelected ? styles.selected : ''}`}
                onClick={() => toggleCategory(category.id)}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: index * 0.08 }}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <span className={styles.categoryIcon}>{category.icon}</span>
                <span className={styles.categoryName}>{category.name}</span>
                {isSelected && (
                  <motion.div 
                    className={styles.checkmark}
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: 'spring', stiffness: 500 }}
                  >
                    ✓
                  </motion.div>
                )}
              </motion.button>
            );
          })}
        </div>
        
        <motion.button
          className={`${styles.startButton} ${selectedCategories.length > 0 ? styles.active : ''}`}
          onClick={handleStart}
          disabled={selectedCategories.length === 0}
          whileHover={selectedCategories.length > 0 ? { scale: 1.02 } : {}}
          whileTap={selectedCategories.length > 0 ? { scale: 0.98 } : {}}
        >
          {selectedCategories.length > 0 
            ? `${selectedCategories.length}개 선택됨 - 시작하기`
            : '카테고리를 선택해주세요'
          }
        </motion.button>
      </motion.div>
      
      {/* Decorative elements */}
      <div className={styles.bgGlow1} />
      <div className={styles.bgGlow2} />
    </div>
  );
};

export default CategorySelect;


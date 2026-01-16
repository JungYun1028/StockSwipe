const generateChartData = (basePrice, isUp) => {
  const data = [];
  let price = basePrice * (isUp ? 0.97 : 1.03);
  
  for (let hour = 9; hour <= 15; hour++) {
    for (let min = 0; min < 60; min += 30) {
      const volatility = (Math.random() - 0.5) * basePrice * 0.02;
      const trend = isUp ? basePrice * 0.005 : -basePrice * 0.005;
      price = price + trend + volatility;
      price = Math.max(price, basePrice * 0.9);
      price = Math.min(price, basePrice * 1.1);
      
      data.push({
        time: `${hour.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}`,
        price: Math.round(price),
      });
    }
  }
  
  return data;
};

export const mockStocks = [
  // This will be populated from API
];

export const categories = [
  { id: 'bio', name: '바이오', icon: '🧬' },
  { id: 'ai', name: 'AI', icon: '🤖' },
  { id: 'ship', name: '선박', icon: '🚢' },
  { id: 'food', name: '식품', icon: '🍜' },
  { id: 'energy', name: '에너지', icon: '⚡' },
  { id: 'semi', name: '반도체', icon: '💾' },
  { id: 'finance', name: '금융', icon: '🏦' },
  { id: 'battery', name: '2차전지', icon: '🔋' },
];

export const getCategoryStocks = (categoryIds) => {
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
  
  const targetCategories = categoryIds.flatMap(id => categoryNameMap[id] || []);
  
  return mockStocks.filter(stock =>
    stock.category.some(cat => targetCategories.includes(cat))
  );
};


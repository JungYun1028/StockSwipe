import axios from 'axios';

// Vite 프록시를 사용하므로 상대 경로 사용
const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const stockAPI = {
  // Get all stocks
  getAllStocks: async () => {
    const response = await api.get('/stocks');
    return response.data;
  },

  // Get stock by ID
  getStockById: async (stockId) => {
    const response = await api.get(`/stocks/${stockId}`);
    return response.data;
  },
  
  // Alias for getStockById
  getStock: async (stockId) => {
    const response = await api.get(`/stocks/${stockId}`);
    return response.data;
  },

  // Get stocks by categories
  getStocksByCategories: async (categories) => {
    const response = await api.post('/stocks/by-categories', categories);
    return response.data;
  },

  // Get all categories
  getCategories: async () => {
    const response = await api.get('/categories');
    return response.data;
  },
  
  // Crawl news for a specific stock
  crawlStockNews: async (stockId, count = 5) => {
    const response = await api.post(`/stocks/${stockId}/news?count=${count}`);
    return response.data;
  },
  
  // Crawl news for all stocks
  crawlAllStocksNews: async (count = 5) => {
    const response = await api.post(`/stocks/news/crawl-all?count=${count}`);
    return response.data;
  },
  
  // Chat with AI
  chat: async (message, stockContext = '') => {
    const response = await api.post('/chat', {
      message,
      stockContext,
    });
    return response.data;
  },
};

export default api;


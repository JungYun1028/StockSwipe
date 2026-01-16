import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AppProvider } from './context/AppContext';
import CategorySelect from './pages/CategorySelect';
import Home from './pages/Home';
import StockDetail from './pages/StockDetail';
import NewsDetail from './pages/NewsDetail';
import WatchList from './pages/WatchList';

function App() {
  return (
    <AppProvider>
      <Router>
        <Routes>
          <Route path="/" element={<CategorySelect />} />
          <Route path="/home" element={<Home />} />
          <Route path="/stock/:id" element={<StockDetail />} />
          <Route path="/news/:stockId/:newsId" element={<NewsDetail />} />
          <Route path="/watchlist" element={<WatchList />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AppProvider>
  );
}

export default App;


package com.stockswipe.repository;

import com.stockswipe.model.News;
import com.stockswipe.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByStock(Stock stock);
    Optional<News> findByNewsIdAndStock(String newsId, Stock stock);
    Optional<News> findByNewsIdAndStock_StockId(String newsId, String stockId);
}

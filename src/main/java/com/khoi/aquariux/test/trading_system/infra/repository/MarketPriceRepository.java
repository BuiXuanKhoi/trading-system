package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {


    @Query(value = "SELECT * FROM market_price order by id DESC LIMIT 2",nativeQuery = true)
    List<MarketPrice> getLatestMarketPrice();
}

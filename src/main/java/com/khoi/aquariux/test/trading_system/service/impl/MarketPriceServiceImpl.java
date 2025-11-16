package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.infra.repository.MarketPriceRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import com.khoi.aquariux.test.trading_system.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class MarketPriceServiceImpl implements MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;
    @Override
    public void saveAll(List<MarketPrice> marketPrices) {
        this.marketPriceRepository.saveAll(marketPrices);
    }
}

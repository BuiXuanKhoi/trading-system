package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.dto.response.BestMarketPriceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.MarketPriceRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import com.khoi.aquariux.test.trading_system.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MarketPriceServiceImpl implements MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;
    @Override
    public void saveAll(List<MarketPrice> marketPrices) {
        this.marketPriceRepository.saveAll(marketPrices);
    }

    @Override
    public Map<CryptoSymbol, BestMarketPriceResponse> getLatestBestMarketPrice() {
        return marketPriceRepository.getLatestMarketPrice()
                .stream()
                .collect(Collectors.toMap(
                        MarketPrice::getSymbol,
                        BestMarketPriceResponse::fromMarketPrice
                ));
    }
}

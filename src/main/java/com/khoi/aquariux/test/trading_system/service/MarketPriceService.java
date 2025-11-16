package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.dto.response.BestMarketPriceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import org.antlr.v4.runtime.misc.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MarketPriceService {


    void saveAll(List<MarketPrice> marketPrices);

    Map<CryptoSymbol, BestMarketPriceResponse> getLatestBestMarketPrice();
}

package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import org.antlr.v4.runtime.misc.Pair;

import java.math.BigDecimal;
import java.util.Map;

public interface MarketPriceService {

    void save(Map<CryptoSymbol, Pair<BigDecimal, BigDecimal>> symbolPriceQuantityMap);
}

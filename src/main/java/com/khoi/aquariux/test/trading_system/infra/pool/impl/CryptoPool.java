package com.khoi.aquariux.test.trading_system.infra.pool.impl;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.connector.BinanceConnector;
import com.khoi.aquariux.test.trading_system.infra.connector.HuobiConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class CryptoPool implements CryptoPoolReadOnly {

    private final Map<CryptoSymbol, Pair<BigDecimal, BigDecimal>> cryptoPriceQuantityCache;

    @Override
    public BigDecimal getCurrentBuyPriceBySymbol(CryptoSymbol symbol) {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getCurrentSellPriceBySymbol(CryptoSymbol symbol) {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getCurrentQuantityBySymbol(CryptoSymbol cryptoSymbol) {
        return BigDecimal.ONE;
    }
}

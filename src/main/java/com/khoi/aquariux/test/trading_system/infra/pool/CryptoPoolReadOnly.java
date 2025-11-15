package com.khoi.aquariux.test.trading_system.infra.pool;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;

import java.math.BigDecimal;

public interface CryptoPoolReadOnly {

    BigDecimal getCurrentBuyPriceBySymbol(CryptoSymbol symbol);

    BigDecimal getCurrentSellPriceBySymbol(CryptoSymbol symbol);

    BigDecimal getCurrentQuantityBySymbol(CryptoSymbol cryptoSymbol);
}

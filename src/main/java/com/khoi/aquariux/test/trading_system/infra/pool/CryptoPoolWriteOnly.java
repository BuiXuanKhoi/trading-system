package com.khoi.aquariux.test.trading_system.infra.pool;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;

import java.math.BigDecimal;

public interface CryptoPoolWriteOnly extends CryptoPoolReadOnly{

    void deductSymbolSell(CryptoSymbol symbol, BigDecimal costQuantity);
    void deductSymbolBuy(CryptoSymbol symbol, BigDecimal costQuantity);


    void renew();
}

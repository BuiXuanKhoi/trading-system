package com.khoi.aquariux.test.trading_system.infra.pool;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;

import java.math.BigDecimal;

public interface CryptoPoolWriteOnly extends CryptoPoolReadOnly{

    void updateSymbolBuy(CryptoSymbol symbol, BigDecimal newQuantity);

    void updateSymbolSell(CryptoSymbol symbol, BigDecimal newQuantity);


    void renew();
}

package com.khoi.aquariux.test.trading_system.infra.pool.impl;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.connector.dto.CryptoPriceResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.connector.BinanceConnector;
import com.khoi.aquariux.test.trading_system.infra.connector.HuobiConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class CryptoPool implements CryptoPoolReadOnly, CryptoPoolWriteOnly {

    private final Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> cryptoPriceQuantityCache;

    private final BinanceConnector binanceConnector;
    private final HuobiConnector huobiConnector;

    public CryptoPool(final BinanceConnector binanceConnector, final HuobiConnector huobiConnector){
        this.binanceConnector = binanceConnector;
        this.huobiConnector = huobiConnector;
        this.cryptoPriceQuantityCache = new HashMap<>();

        cryptoPriceQuantityCache.put(CryptoSymbol.LTCBTC,
                new CryptoPriceResponse.SymbolInfo(
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE
                )
        );

        cryptoPriceQuantityCache.put(CryptoSymbol.ETHBTC,
                new CryptoPriceResponse.SymbolInfo(
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE
                )
        );
    }

    @Override
    public BigDecimal getCurrentBuyPriceBySymbol(CryptoSymbol symbol) {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getCurrentSellPriceBySymbol(CryptoSymbol symbol) {
        return BigDecimal.ONE;
    }

    @Override
    public CryptoPriceResponse.SymbolInfo getSymbolInfoBySymbol(CryptoSymbol symbol) {
        return cryptoPriceQuantityCache.get(symbol);
    }

    @Override
    public BigDecimal getCurrentQuantityBySymbol(CryptoSymbol cryptoSymbol) {
        return BigDecimal.ONE;
    }

    @Override
    public synchronized void updateSymbolBuy(CryptoSymbol symbol, BigDecimal newQuantity) {
        log.info("update buy quantity for symbol {} from {} to {}", symbol, cryptoPriceQuantityCache.get(symbol).getAskQuantity(), newQuantity);
        cryptoPriceQuantityCache.get(symbol).setAskQuantity(newQuantity);
    }

    @Override
    public synchronized void updateSymbolSell(CryptoSymbol symbol, BigDecimal newQuantity) {
        log.info("update sell quantity for symbol {} from {} to {}", symbol, cryptoPriceQuantityCache.get(symbol).getBidQuantity(), newQuantity);
        cryptoPriceQuantityCache.get(symbol).setBidQuantity(newQuantity);
    }

    @Override
    public void renew() {

    }
}

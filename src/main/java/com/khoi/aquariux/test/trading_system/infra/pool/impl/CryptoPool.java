package com.khoi.aquariux.test.trading_system.infra.pool.impl;

import com.khoi.aquariux.test.trading_system.engine.matching.BaseMatchingEngine;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.BinanceConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.HuobiConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import com.khoi.aquariux.test.trading_system.service.MarketPriceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class CryptoPool implements CryptoPoolReadOnly, CryptoPoolWriteOnly {

    private final Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> cryptoPriceQuantityCache;

    private final BinanceConnector binanceConnector;
    private final HuobiConnector huobiConnector;
    private final MarketPriceService marketPriceService;
    @Autowired
    public CryptoPool(final BinanceConnector binanceConnector, final HuobiConnector huobiConnector, final MarketPriceService marketPriceService){
        this.binanceConnector = binanceConnector;
        this.huobiConnector = huobiConnector;
        this.marketPriceService = marketPriceService;
        this.cryptoPriceQuantityCache = new HashMap<>();
        cryptoPriceQuantityCache.put(CryptoSymbol.BTCUSDT, CryptoPriceResponse.SymbolInfo.newInstance());
        cryptoPriceQuantityCache.put(CryptoSymbol.ETHUSDT, CryptoPriceResponse.SymbolInfo.newInstance());
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
    public synchronized void renew() {
        log.info("renew crypto pool at {}", LocalDateTime.now());

        Mono<CryptoPriceResponse> binanceMomo = binanceConnector.fetchLatestPriceAndQuantity();
        Mono<CryptoPriceResponse> huobiMono = huobiConnector.fetchLatestPriceAndQuantity();

        List<MarketPrice> marketPrices = Mono.zip(binanceMomo, huobiMono)
                .map(result -> {
                    Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> binanceResult = result.getT1().getSymbolInfoMap();
                    Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> huobiResult = result.getT2().getSymbolInfoMap();

                    List<MarketPrice> marketPriceList = new ArrayList<>(2);

                    for (CryptoSymbol symbol : CryptoSymbol.getUpdateAbleCryptoSymbol()){

                        BigDecimal binanceAskPrice = binanceResult.get(symbol).getAskPrice();
                        BigDecimal binanceBidPrice = binanceResult.get(symbol).getBidPrice();

                        BigDecimal huobiAskPrice = huobiResult.get(symbol).getAskPrice();
                        BigDecimal huobiBidPrice = huobiResult.get(symbol).getBidPrice();

                        CryptoPriceResponse.SymbolInfo cacheSymbol = cryptoPriceQuantityCache.get(symbol);

                        setBestAskPrice(binanceAskPrice, huobiAskPrice, binanceResult, huobiResult, symbol, cacheSymbol);
                        setBestBidPrice(binanceBidPrice, huobiBidPrice, binanceResult, huobiResult, symbol, cacheSymbol);

                        MarketPrice marketPrice = getMarketPrice(symbol, cacheSymbol);
                        marketPriceList.add(marketPrice);
                    }

                    return marketPriceList;
                }).block();

        marketPriceService.saveAll(marketPrices);
        logPoolUpdated();
    }

    private MarketPrice getMarketPrice(CryptoSymbol symbol, CryptoPriceResponse.SymbolInfo cacheSymbol) {
        MarketPrice marketPrice = new MarketPrice(symbol);
        marketPrice.setBidPrice(cacheSymbol.getBidPrice());
        marketPrice.setBidQuantity(cacheSymbol.getBidQuantity());
        marketPrice.setBidSource(cacheSymbol.getBidSource());
        marketPrice.setAskPrice(cacheSymbol.getAskPrice());
        marketPrice.setAskQuantity(cacheSymbol.getAskQuantity());
        marketPrice.setAskSource(cacheSymbol.getAskSource());
        return marketPrice;
    }

    private void setBestAskPrice(BigDecimal binanceAskPrice, BigDecimal huobiAskPrice,
                                 Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> binanceResult,
                                 Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> huobiResult,
                                 CryptoSymbol symbol,
                                 CryptoPriceResponse.SymbolInfo cacheSymbol){

        CryptoPriceResponse.SymbolInfo bestAskSymbolInfo = binanceAskPrice.compareTo(huobiAskPrice) < 0
                                                        ? binanceResult.get(symbol)
                                                        : huobiResult.get(symbol);

        cacheSymbol.setAskPrice(bestAskSymbolInfo.getAskPrice());
        cacheSymbol.setAskQuantity(bestAskSymbolInfo.getAskQuantity());
        cacheSymbol.setAskSource(bestAskSymbolInfo.getAskSource());
    }

    private void setBestBidPrice(BigDecimal binanceBidPrice, BigDecimal huobiBidPrice,
                                 Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> binanceResult,
                                 Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> huobiResult,
                                 CryptoSymbol symbol,
                                 CryptoPriceResponse.SymbolInfo cacheSymbol){

        CryptoPriceResponse.SymbolInfo bestBidPriceSymbolInfo =  binanceBidPrice.compareTo(huobiBidPrice) > 0
                                                        ? binanceResult.get(symbol)
                                                        : huobiResult.get(symbol);

        cacheSymbol.setBidPrice(bestBidPriceSymbolInfo.getBidPrice());
        cacheSymbol.setBidQuantity(bestBidPriceSymbolInfo.getBidQuantity());
        cacheSymbol.setBidSource(bestBidPriceSymbolInfo.getBidSource());
    }

    private void logPoolUpdated(){
        for (Map.Entry entry : cryptoPriceQuantityCache.entrySet()){
            CryptoPriceResponse.SymbolInfo symbolInfo = (CryptoPriceResponse.SymbolInfo) entry.getValue();
            log.info("info: symbol {} has ask price {} and ask qty {}, has bid price {} and bid qty {}", entry.getKey(),
                    symbolInfo.getAskPrice(),
                    symbolInfo.getAskQuantity(),
                    symbolInfo.getBidPrice(),
                    symbolInfo.getBidQuantity()
            );
        }
    }
}

package com.khoi.aquariux.test.trading_system.infra.pool.impl;

import com.khoi.aquariux.test.trading_system.aspect.ratelimit.RateLimit;
import com.khoi.aquariux.test.trading_system.aspect.ratelimit.TimeUnit;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.BaseConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.BinanceConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.HuobiConnector;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import com.khoi.aquariux.test.trading_system.service.MarketPriceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    private final List<BaseConnector> connectors;
    @Autowired
    public CryptoPool(final BinanceConnector binanceConnector, final HuobiConnector huobiConnector, final MarketPriceService marketPriceService, List<BaseConnector> connectors){
        this.binanceConnector = binanceConnector;
        this.huobiConnector = huobiConnector;
        this.marketPriceService = marketPriceService;
        this.cryptoPriceQuantityCache = new HashMap<>();
        cryptoPriceQuantityCache.put(CryptoSymbol.BTCUSDT, CryptoPriceResponse.SymbolInfo.newInstance());
        cryptoPriceQuantityCache.put(CryptoSymbol.ETHUSDT, CryptoPriceResponse.SymbolInfo.newInstance());
        cryptoPriceQuantityCache.put(CryptoSymbol.USDT, CryptoPriceResponse.SymbolInfo.newInstance());
        this.connectors = connectors;
    }

    @Override
    public BigDecimal getCurrentBuyPriceBySymbol(CryptoSymbol symbol) {
        return cryptoPriceQuantityCache.get(symbol).getAskPrice();
    }

    @Override
    public BigDecimal getCurrentSellPriceBySymbol(CryptoSymbol symbol) {
        return cryptoPriceQuantityCache.get(symbol).getBidPrice();
    }

    @Override
    public CryptoPriceResponse.SymbolInfo getSymbolInfoBySymbol(CryptoSymbol symbol) {
        log.info("get symbol {} info from cache", symbol);
        return cryptoPriceQuantityCache.get(symbol);
    }


    @Override
    public void deductSymbolSell(CryptoSymbol symbol, BigDecimal costQuantity) {
        synchronized (symbol){
            if(cryptoPriceQuantityCache.get(symbol).getBidQuantity().compareTo(costQuantity) < 0)
                throw new MarketCapacityNotEnoughException("market capacity not enough");

            CryptoPriceResponse.SymbolInfo symbolInfo = cryptoPriceQuantityCache.get(symbol);
            symbolInfo.setBidQuantity(symbolInfo.getBidQuantity().subtract(costQuantity));
        }
    }

    @Override
    public void deductSymbolBuy(CryptoSymbol symbol, BigDecimal costQuantity) {
        synchronized (symbol){
            if(cryptoPriceQuantityCache.get(symbol).getAskQuantity().compareTo(costQuantity) < 0)
                throw new MarketCapacityNotEnoughException("market capacity not enough");

            CryptoPriceResponse.SymbolInfo symbolInfo = cryptoPriceQuantityCache.get(symbol);
            symbolInfo.setAskQuantity(symbolInfo.getAskQuantity().subtract(costQuantity));
        }
    }

    @Override
    @RateLimit(duration = 10, timeunit = TimeUnit.SECONDS)
    public synchronized void renew() {
        log.info("renew crypto pool at {}", LocalDateTime.now());
        resetToDefault();

        List<CryptoPriceResponse> responses = Flux.fromIterable(connectors)
                .flatMap(BaseConnector::fetchLatestPriceAndQuantity)
                .collectList()
                .block();

        if (responses == null || responses.isEmpty()){
            log.warn("no market data fetched from any connector");
            return;
        }

        List<MarketPrice> marketPrices = new ArrayList<>(CryptoSymbol.getUpdateAbleCryptoSymbol().size());

        for (CryptoSymbol symbol : CryptoSymbol.getUpdateAbleCryptoSymbol()){

            CryptoPriceResponse.SymbolInfo cacheSymbol = cryptoPriceQuantityCache.get(symbol);
            if (cacheSymbol == null){
                cacheSymbol = CryptoPriceResponse.SymbolInfo.newInstance();
                cryptoPriceQuantityCache.put(symbol, cacheSymbol);
            }

            CryptoPriceResponse.SymbolInfo bestAskInfo = null;
            CryptoPriceResponse.SymbolInfo bestBidInfo = null;

            for (CryptoPriceResponse response : responses){
                if (response == null || response.getSymbolInfoMap() == null){
                    continue;
                }

                CryptoPriceResponse.SymbolInfo symbolInfo = response.getSymbolInfoMap().get(symbol);
                if (symbolInfo == null){
                    continue;
                }

                if (bestAskInfo == null || symbolInfo.getAskPrice().compareTo(bestAskInfo.getAskPrice()) < 0){
                    bestAskInfo = symbolInfo;
                }

                if (bestBidInfo == null || symbolInfo.getBidPrice().compareTo(bestBidInfo.getBidPrice()) > 0){
                    bestBidInfo = symbolInfo;
                }
            }

            if (bestAskInfo != null){
                cacheSymbol.setAskPrice(bestAskInfo.getAskPrice());
                cacheSymbol.setAskQuantity(bestAskInfo.getAskQuantity());
                cacheSymbol.setAskSource(bestAskInfo.getAskSource());
            }

            if (bestBidInfo != null){
                cacheSymbol.setBidPrice(bestBidInfo.getBidPrice());
                cacheSymbol.setBidQuantity(bestBidInfo.getBidQuantity());
                cacheSymbol.setBidSource(bestBidInfo.getBidSource());
            }

            MarketPrice marketPrice = getMarketPrice(symbol, cacheSymbol);
            marketPrices.add(marketPrice);
        }

        marketPriceService.saveAll(marketPrices);
        logPoolUpdated();
    }

    private synchronized void resetToDefault(){
        for (Map.Entry entry : cryptoPriceQuantityCache.entrySet()){
            cryptoPriceQuantityCache.put((CryptoSymbol) entry.getKey(), CryptoPriceResponse.SymbolInfo.newInstance());
        }
    }

    private void compareAndOverwrite(){

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
            log.info("info: symbol {} has ask price {} and ask qty {}, has bid price {} and bid qty {}",
                    entry.getKey(),
                    symbolInfo.getAskPrice(),
                    symbolInfo.getAskQuantity(),
                    symbolInfo.getBidPrice(),
                    symbolInfo.getBidQuantity()
            );
        }
    }
}

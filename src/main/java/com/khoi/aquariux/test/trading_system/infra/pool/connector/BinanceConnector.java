package com.khoi.aquariux.test.trading_system.infra.pool.connector;

import com.khoi.aquariux.test.trading_system.configuration.ConnectionConfig;
import com.khoi.aquariux.test.trading_system.configuration.ExternalConnectionConfig;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.BinanceTickerResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Log4j2
@RequiredArgsConstructor
public class BinanceConnector extends BaseConnector{

    private final ExternalConnectionConfig externalConnectionConfig;
    private final WebClient webClient;
    @Override
    public CryptoSource getType() {
        return CryptoSource.BINANCE;
    }

    @Override
    public Mono<CryptoPriceResponse> fetchLatestPriceAndQuantity() {
        Map<String, ConnectionConfig> map = externalConnectionConfig.getConnectionConfigMap();
        ConnectionConfig cfg = map.get(getType().toString().toLowerCase());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.binance.com")
                        .path(cfg.getPath())
                        .queryParam("symbols", "[\"BTCUSDT\",\"ETHUSDT\"]")
                        .build())
                .retrieve()
                .bodyToMono(BinanceTickerResponse.BinanceTicker[].class)
                .map(tickers -> buildCryptoPriceResponse(
                        new BinanceTickerResponse(Arrays.asList(tickers))
                ));
    }
    private CryptoPriceResponse buildCryptoPriceResponse(BinanceTickerResponse binanceTickerResponse){
        if (Objects.isNull(binanceTickerResponse) || binanceTickerResponse.getBinanceTickers().isEmpty()){
            log.warn("cannot fetch tickers from binance");
            return null;
        }

        Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> symbolInfoMap = new HashMap<>();

        for (BinanceTickerResponse.BinanceTicker ticker : binanceTickerResponse.getBinanceTickers()){
            CryptoSymbol.getSymbolByName(ticker.getSymbol())
                    .ifPresent(symbol -> symbolInfoMap.put(symbol, buildSymbolInfo(ticker)));
        }

        return new CryptoPriceResponse(symbolInfoMap);
    }

    private CryptoPriceResponse.SymbolInfo buildSymbolInfo(BinanceTickerResponse.BinanceTicker ticker){
        log.info("binance response for {} has bid price {} bid qty {}, has ask price {} ask qty {}", ticker.getSymbol(), ticker.getBidPrice(), ticker.getBidQty(), ticker.getAskPrice(), ticker.getAskQty());
        return CryptoPriceResponse.SymbolInfo.builder()
                .askPrice(new BigDecimal(ticker.getAskPrice()))
                .askQuantity(new BigDecimal(ticker.getAskQty()))
                .bidPrice(new BigDecimal(ticker.getBidPrice()))
                .bidQuantity(new BigDecimal(ticker.getBidQty()))
                .askSource(CryptoSource.BINANCE)
                .bidSource(CryptoSource.BINANCE)
                .build();
    }

    @Override
    protected String getQueryParam() {
        return "?symbols=" + "%5B%22BTCUSDT%22" + ",%22ETHUSDT%22%5D";
    }

}

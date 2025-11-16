package com.khoi.aquariux.test.trading_system.infra.pool.connector;

import com.khoi.aquariux.test.trading_system.configuration.ExternalConnectionConfig;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.BinanceTickerResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.HuobTickerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Log4j2
public class HuobiConnector extends BaseConnector{

    private final ExternalConnectionConfig externalConnectionConfig;
    private final WebClient webClient;
    @Override
    public CryptoSource getType() {
        return CryptoSource.HUOBI;
    }

    @Override
    public Mono<CryptoPriceResponse> fetchLatestPriceAndQuantity() {
        String url = buildUrl(externalConnectionConfig);
        log.info("fetch data from huobi with url {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(HuobTickerResponse.class)
                .map(this::buildCryptoPriceResponse);
    }

    private CryptoPriceResponse buildCryptoPriceResponse(HuobTickerResponse response){
        if (Objects.isNull(response) || response.getData().isEmpty()){
            log.warn("cannot get tickers from Huobi");
            return null;
        }

        Map<CryptoSymbol, CryptoPriceResponse.SymbolInfo> symbolInfoMap = new HashMap<>();

        for (HuobTickerResponse.HuobiTickerItem tickerItem : response.getData()){
            CryptoSymbol.getSymbolByName(tickerItem.getSymbol())
                    .ifPresent(symbol -> symbolInfoMap.put(symbol, buildSymbolInfo(tickerItem)));
        }

        return new CryptoPriceResponse(symbolInfoMap);
    }

    private CryptoPriceResponse.SymbolInfo buildSymbolInfo(HuobTickerResponse.HuobiTickerItem tickerItem){
        return CryptoPriceResponse.SymbolInfo.builder()
                .askPrice(tickerItem.getAsk())
                .askQuantity(tickerItem.getAskSize())
                .bidPrice(tickerItem.getBid())
                .bidQuantity(tickerItem.getBidSize())
                .askSource(CryptoSource.HUOBI)
                .bidSource(CryptoSource.HUOBI)
                .build();
    }

    @Override
    protected String getQueryParam() {
        return "";
    }
}

package com.khoi.aquariux.test.trading_system.infra.connector;

import com.khoi.aquariux.test.trading_system.configuration.ExternalConnectionConfig;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.infra.connector.dto.CryptoPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceConnector extends BaseConnector{

    private final ExternalConnectionConfig externalConnectionConfig;
    @Override
    public CryptoSource getType() {
        return CryptoSource.BINANCE;
    }

    @Override
    public CryptoPriceResponse fetchLatestPriceAndQuantity() {
        String url = buildUrl(externalConnectionConfig);

        return null;
    }
}

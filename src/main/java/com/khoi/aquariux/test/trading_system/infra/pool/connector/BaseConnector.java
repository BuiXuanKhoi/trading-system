package com.khoi.aquariux.test.trading_system.infra.pool.connector;

import com.khoi.aquariux.test.trading_system.configuration.ConnectionConfig;
import com.khoi.aquariux.test.trading_system.configuration.ExternalConnectionConfig;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class BaseConnector {

    public abstract CryptoSource getType();

    public String buildUrl(final ExternalConnectionConfig externalConnectionConfig){
        Map<String, ConnectionConfig> connectionConfigMap = externalConnectionConfig.getConnectionConfigMap();
        ConnectionConfig connectionConfig = connectionConfigMap.get(getType().toString().toLowerCase());
        return connectionConfig.getBaseUrl() + connectionConfig.getPath() + getQueryParam();
    }

    public abstract Mono<CryptoPriceResponse> fetchLatestPriceAndQuantity();

    protected abstract String getQueryParam();
}

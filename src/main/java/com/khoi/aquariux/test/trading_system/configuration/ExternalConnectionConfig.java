package com.khoi.aquariux.test.trading_system.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties("external-source")
@Configuration
@Getter
@Setter
public class ExternalConnectionConfig {
    private Map<String, ConnectionConfig> connectionConfigMap;
}

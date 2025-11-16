package com.khoi.aquariux.test.trading_system.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionConfig {
    private String baseUrl;
    private String path;
}

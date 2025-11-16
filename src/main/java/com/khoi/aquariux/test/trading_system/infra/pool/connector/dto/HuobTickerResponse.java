package com.khoi.aquariux.test.trading_system.infra.pool.connector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HuobTickerResponse {
    private String status;
    private long ts;
    private List<HuobiTickerItem> data;

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class HuobiTickerItem{
        private String symbol;
        private BigDecimal bid;
        private BigDecimal bidSize;
        private BigDecimal ask;
        private BigDecimal askSize;

    }
}

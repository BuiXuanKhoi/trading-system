package com.khoi.aquariux.test.trading_system.infra.pool.connector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTickerResponse {

    private List<BinanceTicker> binanceTickers;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BinanceTicker{
        private String symbol;
        private String bidPrice;
        private String bidQty;
        private String askPrice;
        private String askQty;
    }
}

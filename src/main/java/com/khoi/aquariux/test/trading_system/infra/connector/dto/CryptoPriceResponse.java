package com.khoi.aquariux.test.trading_system.infra.connector.dto;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
public class CryptoPriceResponse {
    private Map<CryptoSymbol, SymbolInfo> symbolInfoMap;

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SymbolInfo{
        private BigDecimal bidPrice;
        private BigDecimal askPrice;
        private BigDecimal bidQuantity;
        private BigDecimal askQuantity;
    }
}

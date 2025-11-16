package com.khoi.aquariux.test.trading_system.infra.pool.connector.dto;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class CryptoPriceResponse {
    private Map<CryptoSymbol, SymbolInfo> symbolInfoMap;

    public CryptoPriceResponse(Map<CryptoSymbol, SymbolInfo> symbolInfoMap){
        this.symbolInfoMap = symbolInfoMap;
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SymbolInfo{
        private BigDecimal bidPrice;
        private BigDecimal askPrice;
        private BigDecimal bidQuantity;
        private BigDecimal askQuantity;
        private CryptoSource bidSource;
        private CryptoSource askSource;

        public static SymbolInfo newInstance(){
            return SymbolInfo.builder()
                    .bidPrice(BigDecimal.ZERO)
                    .bidQuantity(BigDecimal.ZERO)
                    .askPrice(BigDecimal.ZERO)
                    .askQuantity(BigDecimal.ZERO)
                    .askSource(CryptoSource.BINANCE)
                    .bidSource(CryptoSource.BINANCE)
                    .build();
        }
    }
}

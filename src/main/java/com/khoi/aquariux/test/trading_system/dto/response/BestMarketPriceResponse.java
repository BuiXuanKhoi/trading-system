package com.khoi.aquariux.test.trading_system.dto.response;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.MarketPrice;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BestMarketPriceResponse {
    private BigDecimal bestBuyPrice;
    private BigDecimal bestBuyQuantity;
    private CryptoSource bestBuySource;
    private BigDecimal bestSellPrice;
    private BigDecimal bestSellQuantity;
    private CryptoSource bestSellSource;
    private Date updatedAt;

    public static BestMarketPriceResponse fromMarketPrice(MarketPrice marketPrice){
        return BestMarketPriceResponse.builder()
                .bestBuyPrice(marketPrice.getAskPrice())
                .bestBuyQuantity(marketPrice.getAskQuantity())
                .bestBuySource(marketPrice.getAskSource())
                .bestSellPrice(marketPrice.getBidPrice())
                .bestSellQuantity(marketPrice.getBidQuantity())
                .bestSellSource(marketPrice.getBidSource())
                .updatedAt(marketPrice.getCreatedDate())
                .build();
    }
}

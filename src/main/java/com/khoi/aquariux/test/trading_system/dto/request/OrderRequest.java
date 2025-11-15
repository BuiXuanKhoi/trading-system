package com.khoi.aquariux.test.trading_system.dto.request;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequest(

        UUID userUuid,
        Boolean isBuy,
        OrderType orderType,
        CryptoSymbol symbol,
        BigDecimal quantity
) {}

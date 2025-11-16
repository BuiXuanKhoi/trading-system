package com.khoi.aquariux.test.trading_system.dto.response;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;


import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record OrderResponse(UUID orderUuId, OrderStatus status, CryptoSymbol symbol, BigDecimal requestAmount, Date createdDate) {

    public static OrderResponse fromOrder(Order order){
        return new OrderResponse(
                order.getOrderUuid(),
                order.getStatus(),
                order.getRequestSymbol(),
                order.getRequestQuantity(),
                order.getCreatedDate()
        );
    }
}

package com.khoi.aquariux.test.trading_system.engine.matching.dto;

import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;

import java.time.Instant;
import java.util.UUID;

public record OrderMessage(
        Long orderId,
        UUID orderUuid,
        OrderType orderType,
        Instant enqueuedAt
) {

    public static OrderMessage fromOrder(Order order, Instant enqueuedAt){
        return new OrderMessage(
                order.getId(),
                order.getOrderUuid(),
                order.getOrderType(),
                enqueuedAt
        );
    }
}

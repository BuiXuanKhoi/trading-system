package com.khoi.aquariux.test.trading_system.engine.matching.strategy;

import com.khoi.aquariux.test.trading_system.engine.matching.dto.OrderMessage;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;

public interface MatchingStrategy {
    OrderType getType();

    void match(OrderMessage message);
}

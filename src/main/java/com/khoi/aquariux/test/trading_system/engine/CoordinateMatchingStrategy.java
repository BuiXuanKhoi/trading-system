package com.khoi.aquariux.test.trading_system.engine;

import com.khoi.aquariux.test.trading_system.enumeration.MatchingStrategy;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.OrderBook;
import org.hibernate.query.Order;

public abstract class CoordinateMatchingStrategy {
    public abstract MatchingStrategy getType();

    public abstract void pushOrder(OrderBook orderBook);
}

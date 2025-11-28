package com.khoi.aquariux.test.trading_system.engine.orderbook;

import com.khoi.aquariux.test.trading_system.engine.matching.dto.OrderMessage;
import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;

import java.util.concurrent.BlockingDeque;

public abstract class OrderBook {
    public abstract OrderBookType getType();

    public abstract void pushOrder(Order order);

    public abstract BlockingDeque<OrderMessage> getBuyOrderQueue();
    public abstract BlockingDeque<OrderMessage> getSellOrderQueue();
}

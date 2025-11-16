package com.khoi.aquariux.test.trading_system.engine.orderbook;

import com.khoi.aquariux.test.trading_system.engine.matching.MatchingEngineFactory;
import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class PriceTimePriorityOrderBook extends OrderBook {
    private final MatchingEngineFactory matchingEngineFactory;

    @Override
    public OrderBookType getType() {
        return OrderBookType.FIFO;
    }

    @Override
    public void pushOrder(Order order) {
        log.info("push order uuid {} to queue", order.getOrderUuid());
        if (order.getOrderType() == OrderType.MARKET){
            matchingEngineFactory.getMatchingEngine(order.isBuy())
                    .getOrderQueue()
                    .offer(order);
        }
    }
}

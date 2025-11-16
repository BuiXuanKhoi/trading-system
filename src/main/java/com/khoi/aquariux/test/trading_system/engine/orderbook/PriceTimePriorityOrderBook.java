package com.khoi.aquariux.test.trading_system.engine.orderbook;

import com.khoi.aquariux.test.trading_system.engine.matching.MatchingEngine;
import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

@Component
@Log4j2
public class PriceTimePriorityOrderBook extends OrderBook {

    private final TreeMap<BigDecimal, BigDecimal> buyBooks;
    private final TreeMap<BigDecimal, BigDecimal> sellBooks;

    private final BlockingQueue<Order> orderQueue;

    @Autowired
    public PriceTimePriorityOrderBook(final MatchingEngine matchingEngine) {
        this.buyBooks = new TreeMap<>(Comparator.reverseOrder());
        this.sellBooks = new TreeMap<>();
        this.orderQueue = matchingEngine.getQueue();
    }

    @Override
    public OrderBookType getType() {
        return OrderBookType.FIFO;
    }

    @Override
    public void pushOrder(Order order) {
        log.info("push order uuid {} to queue", order.getOrderUuid());
        if (order.getOrderType() == OrderType.MARKET){
            orderQueue.offer(order);
        }
    }
}

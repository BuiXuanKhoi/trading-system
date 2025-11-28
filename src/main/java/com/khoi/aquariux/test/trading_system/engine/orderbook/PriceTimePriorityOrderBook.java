package com.khoi.aquariux.test.trading_system.engine.orderbook;

import com.khoi.aquariux.test.trading_system.engine.matching.MatchingEngineFactory;
import com.khoi.aquariux.test.trading_system.engine.matching.dto.OrderMessage;
import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


@Component
@Log4j2
public class PriceTimePriorityOrderBook extends OrderBook {

    private final BlockingDeque<OrderMessage> buyOrderQueue;
    private final BlockingDeque<OrderMessage> sellOrderQueue;

    public PriceTimePriorityOrderBook(){
        this.buyOrderQueue = new LinkedBlockingDeque<>();
        this.sellOrderQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public OrderBookType getType() {
        return OrderBookType.FIFO;
    }

    @Override
    public void pushOrder(Order order) {
        log.info("push order uuid {} to queue", order.getOrderUuid());
        OrderMessage message = OrderMessage.fromOrder(order, Instant.now());
        var orderQueue = order.isBuy() ? buyOrderQueue : sellOrderQueue;
        orderQueue.offer(message);
    }

    @Override
    public BlockingDeque<OrderMessage> getBuyOrderQueue() {
        return this.buyOrderQueue;
    }

    @Override
    public BlockingDeque<OrderMessage> getSellOrderQueue() {
        return this.sellOrderQueue;
    }
}

package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.dto.OrderMessage;
import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import com.khoi.aquariux.test.trading_system.engine.orderbook.OrderBook;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingDeque;

@Component
@Log4j2
public class SellMatchingEngine extends BaseMatchingEngine implements Runnable{
    @Autowired
    public SellMatchingEngine(final MatchingStrategyFactory matchingStrategyFactory, OrderBook orderBook) {
        super(matchingStrategyFactory, orderBook, log);
    }

    @PostConstruct
    void start(){
        Thread.ofVirtual().start(this);
        log.info("--------------------------- sell matching engine initialized ---------------------------------");
    }

    @Override
    public BlockingDeque<OrderMessage> getOrderQueue() {
        return this.orderBook.getSellOrderQueue();
    }

    @Override
    public boolean isBuyMatchingEngine() {
        return false;
    }

    @Override
    public void run() {
        execute();
    }
}

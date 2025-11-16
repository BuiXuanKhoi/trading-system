package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Log4j2
public class SellMatchingEngine extends BaseMatchingEngine implements Runnable{

    @Autowired
    public SellMatchingEngine(final MatchingStrategyFactory matchingStrategyFactory) {
        super(matchingStrategyFactory, log);
    }

    @PostConstruct
    void start(){
        Thread sellMatchingEngineThread = new Thread(this, "sell_matching_engine");
        log.info("--------------------------- sell matching engine started ---------------------------------");
        sellMatchingEngineThread.start();
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

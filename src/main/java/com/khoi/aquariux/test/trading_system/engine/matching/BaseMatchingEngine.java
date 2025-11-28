package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.dto.OrderMessage;
import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import com.khoi.aquariux.test.trading_system.engine.orderbook.OrderBook;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public abstract class BaseMatchingEngine {

    @Getter
    private final BlockingDeque<OrderMessage> orderQueue;
    private final MatchingStrategyFactory matchingStrategyFactory;
    private final ReentrantLock lock;
    private final Condition lockCondition;

    private volatile boolean isPaused;

    protected final OrderBook orderBook;

    private static Logger log = null;

    public BaseMatchingEngine(final MatchingStrategyFactory matchingStrategyFactory, OrderBook orderBook, Logger logger) {
        this.isPaused = false;
        this.lock = new ReentrantLock();
        this.lockCondition = lock.newCondition();
        this.matchingStrategyFactory = matchingStrategyFactory;
        this.orderBook = orderBook;
        this.orderQueue = getOrderQueue();
        log = logger;
    }

    protected void execute(){
        while (true){

            lock.lock();
            while (isPaused){
                try {
                    lockCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }

            try {
                OrderMessage message = orderQueue.take();
                log.info("start matching for order uuid {}", message.orderUuid());

                try {
                    matchingStrategyFactory.getMatchingStrategy(message.orderType()).match(message);
                } catch (MarketCapacityNotEnoughException exception){
                    log.info("push back order to queue to handle later when market capacity filled");
                    orderQueue.addFirst(message);
                    log.info("pause matching engine until market capacity filled");
                    lock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void lock(){
        lock.lock();

        String engineName = isBuyMatchingEngine() ? "BUY" : "SELL";
        log.info("lock {} matching engine", engineName);
        try {
            isPaused = true;
        } finally {
            lock.unlock();
        }
    }

    public void unlock(){
        if (!isPaused){
            return;
        }

        String engineName = isBuyMatchingEngine() ? "BUY" : "SELL";
        log.info("resume {} matching engine", engineName);

        lock.lock();
        try {
            isPaused = false;
            lockCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    abstract boolean isBuyMatchingEngine();

}

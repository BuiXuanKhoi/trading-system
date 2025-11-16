package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public abstract class BaseMatchingEngine {

    @Getter
    private final BlockingQueue<Order> orderQueue;
    private final MatchingStrategyFactory matchingStrategyFactory;
    private final ReentrantLock lock;
    private final Condition lockCondition;

    private volatile boolean isPaused;

    private static Logger log = null;

    public BaseMatchingEngine(final MatchingStrategyFactory matchingStrategyFactory, Logger logger) {
        this.orderQueue = new LinkedBlockingDeque<>();
        this.isPaused = false;
        this.lock = new ReentrantLock();
        this.lockCondition = lock.newCondition();
        this.matchingStrategyFactory = matchingStrategyFactory;
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
                }
            }


            try {
                Order order = orderQueue.take();
                log.info("start matching for order uuid {}", order.getOrderUuid());
                matchingStrategyFactory.getMatchingStrategy(order.getOrderType()).match(order);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MarketCapacityNotEnoughException exception){
                log.info("pause matching engine until market capacity filled");
                lock();
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

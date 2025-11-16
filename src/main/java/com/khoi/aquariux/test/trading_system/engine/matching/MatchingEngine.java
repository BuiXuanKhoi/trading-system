package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategy;
import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Log4j2
public class MatchingEngine implements Runnable{

    private final BlockingQueue<Order> orderQueue;
    private final MatchingStrategyFactory matchingStrategyFactory;
    private final ReentrantLock lock;
    private final Condition lockCondition;

    private Thread matchingEngineThread;

    @Autowired
    public MatchingEngine(final MatchingStrategyFactory matchingStrategyFactory) {
        this.orderQueue = new LinkedBlockingDeque<>();
        this.lock = new ReentrantLock();
        this.lockCondition = lock.newCondition();
        this.matchingStrategyFactory = matchingStrategyFactory;
    }

    @PostConstruct
    void start(){
        matchingEngineThread = new Thread(this, "matching_engine");
        log.info("--------------------------- matching engine started ---------------------------------");
        matchingEngineThread.start();
    }

    public void lock(){

    }

    public void unlock(){

    }

    public BlockingQueue<Order> getQueue(){
        return this.orderQueue;
    }

    @Override
    public void run() {
        while (true){
            Order order = null;
            try {
                order = orderQueue.take();
                log.info("start matching for order uuid {}", order.getOrderUuid());
                matchingStrategyFactory.getMatchingStrategy(order.getOrderType()).match(order);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MarketCapacityNotEnoughException exception){
                try {
                    log.info("pause matching engine until market capacity filled");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}

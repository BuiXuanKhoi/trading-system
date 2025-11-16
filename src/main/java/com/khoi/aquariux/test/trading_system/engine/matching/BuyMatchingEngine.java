package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class BuyMatchingEngine extends BaseMatchingEngine implements Runnable{

    @Autowired
    public BuyMatchingEngine(final MatchingStrategyFactory matchingStrategyFactory) {
        super(matchingStrategyFactory, log);
    }

    @PostConstruct
    void start(){
        Thread buyMatchingEngineThread = new Thread(this, "buy_matching_engine");
        log.info("--------------------------- buy matching engine started ---------------------------------");
        buyMatchingEngineThread.start();
    }

    @Override
    public boolean isBuyMatchingEngine() {
        return true;
    }

    @Override
    public void run() {
        execute();
    }

}

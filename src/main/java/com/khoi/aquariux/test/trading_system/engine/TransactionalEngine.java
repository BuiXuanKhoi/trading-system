package com.khoi.aquariux.test.trading_system.engine;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MarketMatchingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionalEngine {

    @Transactional
    public void execute(Runnable runnable) {
        runnable.run();
    }
}

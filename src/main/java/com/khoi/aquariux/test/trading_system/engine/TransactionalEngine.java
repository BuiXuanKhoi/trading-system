package com.khoi.aquariux.test.trading_system.engine;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionalEngine {

    @Transactional
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    @Async
    public void asyncExecute(Runnable runnable){
        runnable.run();
    }

    @Transactional
    public <T> T execute(Supplier<T> supplier){
        return supplier.get();
    }
}

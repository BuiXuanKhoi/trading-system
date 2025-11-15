package com.khoi.aquariux.test.trading_system.engine.coordinate;

import com.khoi.aquariux.test.trading_system.engine.CoordinateMatchingStrategy;
import com.khoi.aquariux.test.trading_system.enumeration.MatchingStrategy;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.OrderBook;
import org.springframework.stereotype.Component;

@Component
public class CoordinateFIFOMatchingStrategy extends CoordinateMatchingStrategy {
    @Override
    public MatchingStrategy getType() {
        return MatchingStrategy.FIFO;
    }

    @Override
    public void pushOrder(OrderBook orderBook) {}
}

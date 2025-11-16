package com.khoi.aquariux.test.trading_system.engine.matching;

import com.khoi.aquariux.test.trading_system.engine.matching.strategy.MatchingStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MatchingEngineFactory {

    private Map<Boolean, BaseMatchingEngine> matchingEngineMap;

    @Autowired
    public MatchingEngineFactory(List<BaseMatchingEngine> matchingEngines){
        this.matchingEngineMap = matchingEngines.stream()
                .collect(Collectors.toMap(BaseMatchingEngine::isBuyMatchingEngine, Function.identity()));
    }

    public BaseMatchingEngine getMatchingEngine(boolean isBuy){
        return matchingEngineMap.get(isBuy);
    }
}

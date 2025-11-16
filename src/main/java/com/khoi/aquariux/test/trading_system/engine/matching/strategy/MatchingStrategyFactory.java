package com.khoi.aquariux.test.trading_system.engine.matching.strategy;

import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MatchingStrategyFactory {

    private final Map<OrderType, MatchingStrategy> matchingStrategyMap;

    @Autowired
    public MatchingStrategyFactory(List<MatchingStrategy> matchingStrategyList){
        this.matchingStrategyMap = matchingStrategyList.stream()
                .collect(Collectors.toMap(MatchingStrategy::getType, Function.identity()));
    }


    public MatchingStrategy getMatchingStrategy(OrderType orderType){
        return matchingStrategyMap.get(orderType);
    }
}

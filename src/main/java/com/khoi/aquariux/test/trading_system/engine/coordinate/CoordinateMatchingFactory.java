package com.khoi.aquariux.test.trading_system.engine.coordinate;

import com.khoi.aquariux.test.trading_system.engine.CoordinateMatchingStrategy;
import com.khoi.aquariux.test.trading_system.enumeration.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CoordinateMatchingFactory {

    private final Map<MatchingStrategy, CoordinateMatchingStrategy> matchingStrategies;

    @Autowired
    public CoordinateMatchingFactory(List<CoordinateMatchingStrategy> coordinateMatchingStrategies){
        this.matchingStrategies = coordinateMatchingStrategies.stream()
                .collect(Collectors.toMap(CoordinateMatchingStrategy::getType, Function.identity()));
    }

    public CoordinateMatchingStrategy getCoordinateMatchingStrategy(MatchingStrategy matchingStrategy){
        if (!matchingStrategies.containsKey(matchingStrategy)){
            throw new UnsupportedOperationException();
        }

        return matchingStrategies.get(matchingStrategy);
    }


}

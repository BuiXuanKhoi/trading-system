package com.khoi.aquariux.test.trading_system.engine.orderbook;

import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderBookFactory {

    private final Map<OrderBookType, OrderBook> orderBookMap;

    @Autowired
    public OrderBookFactory(List<OrderBook> orderBookList){
        this.orderBookMap = orderBookList.stream()
                .collect(Collectors.toMap(OrderBook::getType, Function.identity()));
    }

    public OrderBook getOrderBook(OrderBookType orderBookType){
        if (!orderBookMap.containsKey(orderBookType)){
            throw new UnsupportedOperationException();
        }

        return orderBookMap.get(orderBookType);
    }
}

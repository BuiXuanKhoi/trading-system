package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.OrderBook;


import java.util.List;
import java.util.UUID;

public interface OrderBookService {

    OrderBook add(OrderRequest request);

    List<OrderBook> findAllByUser(UUID userUuid);
}

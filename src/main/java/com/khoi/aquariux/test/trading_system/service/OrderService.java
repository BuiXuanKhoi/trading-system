package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.dto.response.OrderDetailResponse;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import org.aspectj.weaver.ast.Or;


import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order placeMarketOrder(OrderRequest orderRequest);

    List<Order> findAllByUser(UUID userUuid);

    void updateStatus(Order order, OrderStatus orderStatus);

    List<OrderDetailResponse> findAllByUserUuid(UUID userUuid);
}

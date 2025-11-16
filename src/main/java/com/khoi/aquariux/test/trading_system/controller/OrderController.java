package com.khoi.aquariux.test.trading_system.controller;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.dto.response.OrderResponse;
import com.khoi.aquariux.test.trading_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(
            @RequestBody @Validated OrderRequest request
    ){
        return OrderResponse.fromOrder(orderService.placeMarketOrder(request));
    }
}

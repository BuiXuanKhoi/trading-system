package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.OrderBook;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {

    UUID recordForOrder(OrderBook orderBook, BigDecimal cryptoAmount, BigDecimal executionAmount);
}

package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {


    Transaction addNewByOrder(Order order, BigDecimal receiveQuantity, BigDecimal costQuantity);
}

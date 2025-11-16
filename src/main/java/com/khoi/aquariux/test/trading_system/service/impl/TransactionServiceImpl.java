package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.enumeration.TransactionStatus;
import com.khoi.aquariux.test.trading_system.infra.repository.TransactionRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Transaction;
import com.khoi.aquariux.test.trading_system.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction addNewByOrder(Order order, TransactionStatus status, BigDecimal receiveQuantity, BigDecimal costQuantity) {
        Transaction transaction =  Transaction.builder()
                                            .isBuy(order.isBuy())
                                            .order(order)
                                            .transactionUuid(UUID.randomUUID())
                                            .requestSymbol(order.getRequestSymbol())
                                            .requestQuantity(receiveQuantity)
                                            .usedSymbol(order.getUsedSymbol())
                                            .usedQuantity(costQuantity)
                                            .status(status)
                                            .build();

        return saveAndFlush(transaction);
    }


    private Transaction saveAndFlush(Transaction transaction){
        String action = Objects.nonNull(transaction.getId()) ? "UPDATED" : "CREATED";
        Transaction savedTransaction = transactionRepository.saveAndFlush(transaction);
        log.info("finish {} transaction with uuid {}", action, savedTransaction.getTransactionUuid());
        return savedTransaction;
    }
}

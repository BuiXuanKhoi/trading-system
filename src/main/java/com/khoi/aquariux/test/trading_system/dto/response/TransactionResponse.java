package com.khoi.aquariux.test.trading_system.dto.response;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.TransactionStatus;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse{

    private UUID transactionUuid;
    private boolean isBuy;

    private CryptoSymbol requestSymbol;
    private BigDecimal requestQuantity;
    private CryptoSymbol usedSymbol;
    private BigDecimal usedQuantity;
    private TransactionStatus status;


    public static TransactionResponse fromTransaction(Transaction transaction){
        return TransactionResponse.builder()
                .transactionUuid(transaction.getTransactionUuid())
                .isBuy(transaction.getIsBuy())
                .requestSymbol(transaction.getRequestSymbol())
                .requestQuantity(transaction.getRequestQuantity())
                .usedSymbol(transaction.getUsedSymbol())
                .usedQuantity(transaction.getUsedQuantity())
                .status(transaction.getStatus())
                .build();
    }
}

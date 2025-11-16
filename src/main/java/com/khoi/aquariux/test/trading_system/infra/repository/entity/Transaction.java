package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.TransactionStatus;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "transactions")
@AllArgsConstructor
public class Transaction extends BaseEntity {

    @Column(name = "transaction_uuid")
    private UUID transactionUuid;

    @Column(name = "is_buy")
    private Boolean isBuy;

    @Column(name = "request_symbol", precision = 20, scale = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoSymbol requestSymbol;

    @Column(name = "used_quantity", precision = 20, scale = 8, nullable = false)
    private BigDecimal usedQuantity;

    @Column(name = "used_symbol", precision = 20, scale = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoSymbol usedSymbol;

    @Column(name = "request_quantity", precision = 20, scale = 8, nullable = false)
    private BigDecimal requestQuantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}

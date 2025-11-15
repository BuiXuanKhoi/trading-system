package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "order_book")
public class OrderBook extends BaseEntity {


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "requested_usdt_amount")
    private BigDecimal requestedUsdtAmount;

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private CryptoSymbol symbol;

    @Column(name = "crypto_amount")
    private BigDecimal cryptoAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean side;

    @Column(name = "order_uuid")
    private UUID orderUuid;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "limit_price")
    private BigDecimal limitPrice;

    @Column(name = "execution_price")
    private BigDecimal executionPrice;
}

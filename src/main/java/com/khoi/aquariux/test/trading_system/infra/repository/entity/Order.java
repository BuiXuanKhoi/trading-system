package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_quantity", precision = 20, scale = 8, nullable = false)
    private BigDecimal requestQuantity;

    @Column(name = "request_symbol", precision = 20, scale = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoSymbol requestSymbol;

    @Column(name = "used_quantity", precision = 20, scale = 8, nullable = false)
    private BigDecimal usedQuantity;

    @Column(name = "used_symbol", precision = 20, scale = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoSymbol usedSymbol;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "is_buy")
    private boolean isBuy;

    @Column(name = "order_uuid")
    private UUID orderUuid;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "limit_price", precision = 20, scale = 8, nullable = false)
    private BigDecimal limitPrice;

    @Column(name = "execution_quantity", precision = 20, scale = 8)
    private BigDecimal executionQuantity;
}

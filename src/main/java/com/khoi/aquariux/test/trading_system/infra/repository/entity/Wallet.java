package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "wallets")
@AllArgsConstructor
public class Wallet extends BaseEntity {

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private CryptoSymbol symbol;

    @Column(name = "available_balance", precision = 20, scale = 8, nullable = false)
    private BigDecimal availableBalance;

    @Column(name = "locked_balance", precision = 20, scale = 8, nullable = false)
    private BigDecimal lockedBalance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private CryptoSymbol symbol;

    @Column(name = "available_balance")
    private BigDecimal availableBalance;

    @Column(name = "locked_balance")
    private BigDecimal lockedBalance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

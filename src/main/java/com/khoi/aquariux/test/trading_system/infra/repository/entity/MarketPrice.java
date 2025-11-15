package com.khoi.aquariux.test.trading_system.infra.repository.entity;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSource;
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
@Table(name = "market_price")
public class MarketPrice extends BaseEntity {

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private CryptoSymbol symbol;

    @Column(name = "bid_price")
    private BigDecimal bidPrice;

    @Column(name = "bid_quantity")
    private BigDecimal bidQuantity;

    @Column(name = "bid_source")
    private CryptoSource bidSource;

    @Column(name = "ask_price")
    private BigDecimal askPrice;

    @Column(name = "ask_quantity")
    private BigDecimal askQuantity;

    @Column(name = "ask_source")
    @Enumerated(EnumType.STRING)
    private CryptoSource askSource;
}

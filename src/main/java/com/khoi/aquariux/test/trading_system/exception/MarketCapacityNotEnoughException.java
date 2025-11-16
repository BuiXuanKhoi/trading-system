package com.khoi.aquariux.test.trading_system.exception;

import com.khoi.aquariux.test.trading_system.exception.base.DomainException;

public class MarketCapacityNotEnoughException extends DomainException {
    public MarketCapacityNotEnoughException(String message, Object... args) {
        super(message, args);
    }

    public MarketCapacityNotEnoughException(String message) {
        super(message);
    }
}

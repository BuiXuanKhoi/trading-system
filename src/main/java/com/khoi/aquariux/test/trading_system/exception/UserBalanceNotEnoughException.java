package com.khoi.aquariux.test.trading_system.exception;

import com.khoi.aquariux.test.trading_system.exception.base.DomainException;

public class UserBalanceNotEnoughException extends DomainException {
    public UserBalanceNotEnoughException(String message, Object... args) {
        super(message, args);
    }

    public UserBalanceNotEnoughException(String message) {
        super(message);
    }
}

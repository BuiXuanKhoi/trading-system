package com.khoi.aquariux.test.trading_system.exception;

import com.khoi.aquariux.test.trading_system.exception.base.ThirdPartyIssueException;

public class CircuitBreakerException extends ThirdPartyIssueException {
    public CircuitBreakerException(String message, Object... args) {
        super(message, args);
    }

    public CircuitBreakerException(String message) {
        super(message);
    }
}

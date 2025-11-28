package com.khoi.aquariux.test.trading_system.exception;

import com.khoi.aquariux.test.trading_system.exception.base.ThirdPartyIssueException;

public class RateLimitExceedException extends ThirdPartyIssueException {
    public RateLimitExceedException(String message, Object... args) {
        super(message, args);
    }

    public RateLimitExceedException(String message) {
        super(message);
    }
}

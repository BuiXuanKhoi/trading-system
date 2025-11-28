package com.khoi.aquariux.test.trading_system.exception.base;

public class ThirdPartyIssueException extends DomainException{
    public ThirdPartyIssueException(String message, Object... args) {
        super(message, args);
    }

    public ThirdPartyIssueException(String message) {
        super(message);
    }
}

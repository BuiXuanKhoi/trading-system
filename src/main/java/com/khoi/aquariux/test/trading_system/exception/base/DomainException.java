package com.khoi.aquariux.test.trading_system.exception.base;

public class DomainException extends RuntimeException{

    public DomainException(String message, Object... args) {
        super(String.format(message, args));
    }

    public DomainException(String message){
        super(message);
    }
}

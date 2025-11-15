package com.khoi.aquariux.test.trading_system.exception;

import com.khoi.aquariux.test.trading_system.exception.base.DomainException;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message, Object... args) {
        super(message, args);
    }

    public ResourceNotFoundException(String message){
        super(message);
    }
}

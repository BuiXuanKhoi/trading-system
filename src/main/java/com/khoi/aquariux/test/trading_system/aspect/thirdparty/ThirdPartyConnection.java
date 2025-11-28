package com.khoi.aquariux.test.trading_system.aspect.thirdparty;

import com.khoi.aquariux.test.trading_system.aspect.circuitbreaker.CircuitBreaker;
import com.khoi.aquariux.test.trading_system.aspect.ratelimit.RateLimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CircuitBreaker
public @interface ThirdPartyConnection {
    boolean enableCircuitBreaker() default false;

    boolean enableRateLimit() default false;
}

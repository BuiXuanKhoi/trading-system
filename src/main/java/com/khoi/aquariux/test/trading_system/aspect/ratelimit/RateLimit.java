package com.khoi.aquariux.test.trading_system.aspect.ratelimit;

import lombok.NonNull;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int value() default 0;

    int duration(); // duration in seconds

    TimeUnit timeunit();
}

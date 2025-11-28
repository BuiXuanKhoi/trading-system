package com.khoi.aquariux.test.trading_system.aspect.ratelimit;

import com.khoi.aquariux.test.trading_system.exception.RateLimitExceedException;
import com.khoi.aquariux.test.trading_system.utils.CacheUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class RateLimitAspect {


    @Around("@annotation(rateLimit)")
    public Object rateLimitTracking(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        int duration = rateLimit.duration();
        log.info("check rate limit ");
        CacheUtil.overwrite("limit", 10);
        CacheUtil.overwrite("circuit-status", "ON");


        return joinPoint.proceed();

    }
}

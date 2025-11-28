package com.khoi.aquariux.test.trading_system.aspect.thirdparty;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ThirdPartyConnectionAspect {

    @Around("@annotation(thirdPartyConnection)")
    public Object handleThirdPartyConnection(ProceedingJoinPoint joinPoint, ThirdPartyConnection thirdPartyConnection) throws Throwable {

        return joinPoint.proceed();
    }
}

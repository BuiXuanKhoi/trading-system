package com.khoi.aquariux.test.trading_system.aspect;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.base.BaseEntity;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
@Log4j2
public class EntityUpdateTracking {

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..)) || " +
            "execution(* org.springframework.data.jpa.repository.JpaRepository+.saveAndFlush(..)) ")
    public Object logUpdateEntity(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseEntity entity = (BaseEntity) joinPoint.getArgs()[0];

        String action  = Objects.isNull(entity.getId()) ? "CREATE" : "UPDATE";
        Object result = joinPoint.proceed();

        log.info("finish {} for {} with id {}", action, entity.getClass().getSimpleName().toLowerCase(), entity.getId());

        return result;
    }
}

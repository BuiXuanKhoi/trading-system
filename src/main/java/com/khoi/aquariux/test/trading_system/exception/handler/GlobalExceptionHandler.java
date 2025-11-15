package com.khoi.aquariux.test.trading_system.exception.handler;

import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, Object> handleResourceNotFoundException(
            HttpServletRequest request,
            ResourceNotFoundException exception
    ){
        log.warn("handle resource not found exception, endpoint {}", request.getRequestURI());
        return Map.of("errors", List.of(exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleUnexpectedException(
            HttpServletRequest request,
            Exception exception
    ){
        log.error("handle unexpected {} exception, endpoint {}", exception.getClass(), request.getRequestURI());
        return Map.of("errors", List.of(exception.getMessage()));
    }
}

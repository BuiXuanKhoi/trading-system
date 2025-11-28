package com.khoi.aquariux.test.trading_system.utils;

import com.khoi.aquariux.test.trading_system.exception.TypeNotMatchException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class CacheUtil {


    private final static Map<String, Object> cache = new HashMap<>();


    public static void overwrite(String key, Object value){
        Object existingValue = cache.get(key);
        if (existingValue != null && Objects.nonNull(value)) {
            Class<?> currentType = existingValue.getClass();
            Class<?> newType = value.getClass();
            if (!currentType.equals(newType)) {
                throw new TypeNotMatchException("Key '%s' expects %s but received %s", key, currentType.getSimpleName(), newType.getSimpleName());
            }
        }
        cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key){
        return (T) cache.get(key);
    }




}

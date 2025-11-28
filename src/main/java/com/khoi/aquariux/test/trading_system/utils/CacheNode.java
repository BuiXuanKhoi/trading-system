package com.khoi.aquariux.test.trading_system.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CacheNode <V> {

    private V value;

    private Date createdDate;

    private Date lastModifiedDate;

    public CacheNode(V value) {
        this.value = value;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }
}

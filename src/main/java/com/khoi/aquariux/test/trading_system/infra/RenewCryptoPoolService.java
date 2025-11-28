package com.khoi.aquariux.test.trading_system.infra;

import com.khoi.aquariux.test.trading_system.engine.matching.BaseMatchingEngine;
import com.khoi.aquariux.test.trading_system.exception.RateLimitExceedException;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class RenewCryptoPoolService {

    private final CryptoPoolWriteOnly cryptoPool;

    private final List<BaseMatchingEngine> matchingEngines;

    @Autowired
    public RenewCryptoPoolService(CryptoPoolWriteOnly cryptoPoolWriteOnly, List<BaseMatchingEngine> matchingEngines){
        this.cryptoPool = cryptoPoolWriteOnly;
        this.matchingEngines = matchingEngines;
    }

    public void execute(){
        try {
            cryptoPool.renew();
        } catch (RateLimitExceedException exception){
            return;
        }

        for (BaseMatchingEngine matchingEngine : matchingEngines){
            matchingEngine.unlock();
        }
    }
}

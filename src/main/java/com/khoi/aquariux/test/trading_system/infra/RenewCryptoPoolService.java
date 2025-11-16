package com.khoi.aquariux.test.trading_system.infra;

import com.khoi.aquariux.test.trading_system.engine.matching.BaseMatchingEngine;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class RenewCryptoPoolService {

    private final CryptoPoolWriteOnly cryptoPool;

    private final List<BaseMatchingEngine> matchingEngines;

    public RenewCryptoPoolService(CryptoPoolWriteOnly cryptoPoolWriteOnly, List<BaseMatchingEngine> matchingEngines){
        this.cryptoPool = cryptoPoolWriteOnly;
        this.matchingEngines = matchingEngines;
    }

    public void execute(){
        cryptoPool.renew();
        for (BaseMatchingEngine matchingEngine : matchingEngines){
            matchingEngine.unlock();
        }
    }
}

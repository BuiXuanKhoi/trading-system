package com.khoi.aquariux.test.trading_system.job;

import com.khoi.aquariux.test.trading_system.infra.RenewCryptoPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class PriceAggregateJob {

    private final RenewCryptoPoolService renewCryptoPoolService;

    @Scheduled(fixedRate = 10_000)
    public void run(){
        log.info("run job renew crypto pool");
        renewCryptoPoolService.execute();
    }
}

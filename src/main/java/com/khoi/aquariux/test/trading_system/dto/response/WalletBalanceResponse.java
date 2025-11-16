package com.khoi.aquariux.test.trading_system.dto.response;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {

    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;

    private Date lastUpdatedAt;

    public static WalletBalanceResponse fromWallet(Wallet wallet){
        return WalletBalanceResponse.builder()
                .availableBalance(wallet.getAvailableBalance())
                .lockedBalance(wallet.getLockedBalance())
                .lastUpdatedAt(wallet.getLastModifiedDate())
                .build();
    }
}

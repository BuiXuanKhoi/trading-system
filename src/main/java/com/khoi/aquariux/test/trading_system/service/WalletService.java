package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.dto.response.WalletBalanceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WalletService {
    List<Wallet> findAllWalletByUser(Long userId);

    Wallet findWalletByUserAndSymbol(User user, CryptoSymbol symbol);

    void acquireLockFund(Long walletId, CryptoSymbol symbol, BigDecimal lockedQuantity);

    void deduct(Wallet wallet, BigDecimal newBalance);
    void depositTo(Wallet wallet, BigDecimal newBalance);

    Map<CryptoSymbol, WalletBalanceResponse> getWalletsByUserUuid(UUID userUuid);
}

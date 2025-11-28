package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.dto.response.WalletBalanceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
import com.khoi.aquariux.test.trading_system.exception.UserBalanceNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.WalletRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.UserService;
import com.khoi.aquariux.test.trading_system.service.WalletService;import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;


    @Override
    @Transactional(readOnly = true)
    public Wallet findWalletByUserAndSymbol(User user, CryptoSymbol symbol) {
        return walletRepository.findWalletByUserAndSymbol(user.getId(), symbol.toString())
                .orElseThrow(() -> new ResourceNotFoundException("wallet %s not found for user uuid: %s", symbol.toString(), user.getUserUuid().toString()));
    }

    @Override
    public void acquireLockFund(Long walletId, CryptoSymbol symbol, BigDecimal lockedQuantity) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("not found wallet for symbol %s", symbol.name()));
        
        if (wallet.getAvailableBalance().compareTo(lockedQuantity) < 0){
            throw new UserBalanceNotEnoughException("not enough balance for symbol %s", symbol.name());
        }

        BigDecimal availableBalance = wallet.getAvailableBalance();
        BigDecimal lockedBalance = wallet.getLockedBalance();

        BigDecimal newAvailableBalance = availableBalance.subtract(lockedQuantity);
        BigDecimal newLockedBalance = lockedBalance.add(lockedQuantity);

        wallet.setAvailableBalance(newAvailableBalance);
        wallet.setLockedBalance(newLockedBalance);

        walletRepository.saveAndFlush(wallet);
    }

    @Override
    @Transactional
    public void deduct(Wallet wallet, BigDecimal quantity) {
        log.info("deduct from {} wallet", wallet.getSymbol());
        BigDecimal newBalance = wallet.getAvailableBalance().subtract(quantity);
        updateBalance(wallet, newBalance);
    }

    @Override
    @Transactional
    public void depositTo(Wallet wallet, BigDecimal quantity) {
        log.info("deposit to {} wallet", wallet.getSymbol());
        BigDecimal newBalance = wallet.getAvailableBalance().add(quantity);
        updateBalance(wallet, newBalance);
    }

    @Override
    public Map<CryptoSymbol, WalletBalanceResponse> getWalletsByUserUuid(UUID userUuid) {
        User user = userService.findUserByUuid(userUuid);

        List<Wallet> wallets = walletRepository.findAllWalletByUser(user.getId());

        return wallets.stream()
                .collect(Collectors.toMap(
                        Wallet::getSymbol,
                        WalletBalanceResponse::fromWallet
                ));
    }

    private void updateBalance(Wallet wallet, BigDecimal newBalance){
        wallet.setAvailableBalance(newBalance);
        walletRepository.saveAndFlush(wallet);
    }
}

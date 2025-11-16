package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
import com.khoi.aquariux.test.trading_system.exception.UserBalanceNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.repository.WalletRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public List<Wallet> findAllWalletByUser(Long userId) {
        return null;
    }

    @Override
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

        saveAndFlush(wallet);
    }

    @Override
    public void deduct(Wallet wallet, BigDecimal quantity) {
        log.info("deduct from {} wallet of user uuid {}", wallet.getSymbol(), wallet.getUser().getUserUuid());
        BigDecimal newBalance = wallet.getAvailableBalance().subtract(quantity);
        updateBalance(wallet, newBalance);
    }

    @Override
    public void depositTo(Wallet wallet, BigDecimal quantity) {
        log.info("deposit to {} wallet of user uuid {}", wallet.getSymbol(), wallet.getUser().getUserUuid());
        BigDecimal newBalance = wallet.getAvailableBalance().add(quantity);
        updateBalance(wallet, newBalance);
    }

    private void updateBalance(Wallet wallet, BigDecimal newBalance){
        wallet.setAvailableBalance(newBalance);
        saveAndFlush(wallet);
    }

    private Wallet saveAndFlush(Wallet wallet){
        String action = Objects.nonNull(wallet.getId()) ? "UPDATED" : "CREATED";
        Wallet savedWallet = walletRepository.saveAndFlush(wallet);
        log.info("finish {} for wallet id {}", action, wallet.getId());

        return savedWallet;
    }
}

package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.engine.TransactionalEngine;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
import com.khoi.aquariux.test.trading_system.infra.repository.UserRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.UserService;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public User findUserByUuid(UUID userUuid) {
        log.info("handle find user for user uuid {}", userUuid);
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for UUID: %s", userUuid.toString()));
    }

    @Override
    @Transactional
    public User findOrDefault(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> createNewUser(username));
    }

    public User createNewUser(String username){
        User defaultUser = User.builder()
                .username(username)
                .userUuid(UUID.randomUUID())
                .build();

        List<Wallet> defaultWallets = buildDefaultWalletForNewUser(defaultUser);

        defaultUser.setWallets(defaultWallets);

        return userRepository.saveAndFlush(defaultUser);
    }

    private List<Wallet> buildDefaultWalletForNewUser(User user){
        return List.of(
                Wallet.builder()
                        .user(user)
                        .symbol(CryptoSymbol.USDT)
                        .lockedBalance(BigDecimal.ZERO)
                        .availableBalance(BigDecimal.valueOf(50_000))
                        .build(),
                Wallet.builder()
                        .user(user)
                        .symbol(CryptoSymbol.BTCUSDT)
                        .lockedBalance(BigDecimal.ZERO)
                        .availableBalance(BigDecimal.ZERO)
                        .build(),
                Wallet.builder()
                        .user(user)
                        .symbol(CryptoSymbol.ETHUSDT)
                        .lockedBalance(BigDecimal.ZERO)
                        .availableBalance(BigDecimal.ZERO)
                        .build()
        );
    }
}

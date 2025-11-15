package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query(value = "SELECT * from wallets where user_id = :userId and symbol = :symbol",nativeQuery = true)
    Optional<Wallet> findWalletByUserAndSymbol(Long userId, String symbol);
}

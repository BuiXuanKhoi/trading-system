package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {


    Optional<Wallet> findWalletByUserAndSymbol(Long userId, String symbol);

    @Query(value = "SELECT * from wallets where user_id = :userId",nativeQuery = true)
    List<Wallet> findAllWalletByUser(Long userId);
}

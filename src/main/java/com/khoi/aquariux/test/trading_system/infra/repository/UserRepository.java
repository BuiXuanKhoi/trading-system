package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserUuid(UUID userUuId);
}

package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

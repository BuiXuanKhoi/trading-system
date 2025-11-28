package com.khoi.aquariux.test.trading_system.infra.repository;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT DISTINCT ords.* FROM orders ords " +
            "LEFT JOIN transactions txns ON ords.id = txns.order_id " +
            "WHERE ords.user_id = :userId",
            nativeQuery = true)
    List<Order> getAllByUserId(Long userId);
}

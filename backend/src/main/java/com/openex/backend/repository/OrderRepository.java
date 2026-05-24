package com.openex.backend.repository;

import com.openex.backend.model.Order;
import com.openex.backend.model.Order.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}

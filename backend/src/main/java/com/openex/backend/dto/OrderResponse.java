package com.openex.backend.dto;

import com.openex.backend.model.Order.OrderSide;
import com.openex.backend.model.Order.OrderStatus;
import com.openex.backend.model.Order.OrderType;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        Long userId,
        OrderSide side,
        OrderType type,
        OrderStatus status,
        String baseCurrency,
        String quoteCurrency,
        BigDecimal quantity,
        BigDecimal filledQuantity,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt
) {
}

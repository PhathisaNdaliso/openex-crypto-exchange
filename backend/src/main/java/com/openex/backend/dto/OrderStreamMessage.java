package com.openex.backend.dto;

import com.openex.backend.model.Order.OrderStatus;
import java.time.Instant;

public record OrderStreamMessage(
        Long orderId,
        Long userId,
        OrderStatus status,
        String eventType,
        Instant timestamp
) {
}

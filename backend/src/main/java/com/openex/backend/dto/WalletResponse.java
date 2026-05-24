package com.openex.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletResponse(
        Long id,
        Long userId,
        String currency,
        BigDecimal balance,
        BigDecimal lockedBalance,
        Instant createdAt,
        Instant updatedAt
) {
}

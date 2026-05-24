package com.openex.backend.dto;

import java.math.BigDecimal;

public record WalletRequest(
        Long userId,
        String currency,
        BigDecimal balance,
        BigDecimal lockedBalance
) {
}

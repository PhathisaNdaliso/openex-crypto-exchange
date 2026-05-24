package com.openex.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketUpdateMessage(
        String symbol,
        BigDecimal lastPrice,
        BigDecimal change24h,
        Instant timestamp
) {
}

package com.openex.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeUpdateMessage(
        String symbol,
        BigDecimal price,
        BigDecimal volume,
        String side,
        BigDecimal changePercent,
        Instant timestamp
) {
}

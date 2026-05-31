package com.openex.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketTickerResponse(
        String symbol,
        BigDecimal price,
        BigDecimal changePercent,
        BigDecimal volume24h,
        BigDecimal marketCap,
        Instant timestamp
) {
}

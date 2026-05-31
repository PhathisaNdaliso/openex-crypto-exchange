package com.openex.backend.dto;

import java.math.BigDecimal;

public record MarketMetricsResponse(
        BigDecimal volume24h,
        BigDecimal marketCap,
        BigDecimal btcDominance,
        long activeTradesCount
) {
}

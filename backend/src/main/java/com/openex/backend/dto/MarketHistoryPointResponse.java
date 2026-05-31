package com.openex.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketHistoryPointResponse(
        Instant timestamp,
        BigDecimal price,
        BigDecimal volume
) {
}

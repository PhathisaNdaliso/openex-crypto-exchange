package com.openex.backend.dto;

import java.util.List;
import java.util.Map;

public record MarketOverviewResponse(
        List<MarketTickerResponse> tickers,
        Map<String, List<MarketHistoryPointResponse>> history,
        MarketMetricsResponse metrics
) {
}

package com.openex.backend.controller;

import com.openex.backend.dto.MarketOverviewResponse;
import com.openex.backend.service.MarketDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketDataService marketDataService;

    public MarketController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/overview")
    public MarketOverviewResponse getMarketOverview() {
        return marketDataService.getOverview();
    }
}

package com.openex.backend.service;

import com.openex.backend.dto.TradeUpdateMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TradeFeedService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MarketDataService marketDataService;

    public TradeFeedService(SimpMessagingTemplate messagingTemplate, MarketDataService marketDataService) {
        this.messagingTemplate = messagingTemplate;
        this.marketDataService = marketDataService;
    }

    @Scheduled(initialDelay = 1000, fixedRate = 1800)
    public void publishSimulatedTrade() {
        TradeUpdateMessage message = marketDataService.generateNextTrade();
        messagingTemplate.convertAndSend("/topic/trades", message);
    }
}

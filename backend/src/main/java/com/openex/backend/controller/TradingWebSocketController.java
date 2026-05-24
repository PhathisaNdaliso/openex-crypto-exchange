package com.openex.backend.controller;

import com.openex.backend.dto.MarketUpdateMessage;
import com.openex.backend.dto.OrderStreamMessage;
import com.openex.backend.dto.WebSocketAckMessage;
import java.time.Instant;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class TradingWebSocketController {

    @MessageMapping("/market.broadcast")
    @SendTo("/topic/market")
    public MarketUpdateMessage broadcastMarketUpdate(MarketUpdateMessage message) {
        return new MarketUpdateMessage(
                message.symbol(),
                message.lastPrice(),
                message.change24h(),
                message.timestamp() != null ? message.timestamp() : Instant.now()
        );
    }

    @MessageMapping("/orders.broadcast")
    @SendTo("/topic/orders")
    public OrderStreamMessage broadcastOrderUpdate(OrderStreamMessage message) {
        return new OrderStreamMessage(
                message.orderId(),
                message.userId(),
                message.status(),
                message.eventType(),
                message.timestamp() != null ? message.timestamp() : Instant.now()
        );
    }

    @MessageMapping("/ping")
    @SendToUser("/queue/status")
    public WebSocketAckMessage ping() {
        return new WebSocketAckMessage("WebSocket connection active", Instant.now());
    }
}

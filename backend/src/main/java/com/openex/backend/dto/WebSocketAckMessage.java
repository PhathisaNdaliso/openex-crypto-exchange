package com.openex.backend.dto;

import java.time.Instant;

public record WebSocketAckMessage(
        String message,
        Instant timestamp
) {
}

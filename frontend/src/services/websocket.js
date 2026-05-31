import { Client } from "@stomp/stompjs";

function resolveBrokerUrl() {
  if (import.meta.env.VITE_WS_URL) {
    return import.meta.env.VITE_WS_URL;
  }

  const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
  return `${protocol}//${window.location.host}/ws`;
}

export function createTradeSocket({ onTrade, onStatus, onError }) {
  const client = new Client({
    brokerURL: resolveBrokerUrl(),
    reconnectDelay: 4000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      onStatus?.("Connected to /topic/trades");
      client.subscribe("/topic/trades", (message) => {
        const payload = JSON.parse(message.body);
        onTrade?.(payload);
      });
    },
    onStompError: (frame) => {
      onStatus?.("Broker error");
      onError?.(frame);
    },
    onWebSocketClose: () => {
      onStatus?.("Reconnecting to trade feed...");
    },
    onWebSocketError: (event) => {
      onStatus?.("Connection failed");
      onError?.(event);
    },
  });

  client.activate();

  return {
    disconnect() {
      client.deactivate();
    },
  };
}

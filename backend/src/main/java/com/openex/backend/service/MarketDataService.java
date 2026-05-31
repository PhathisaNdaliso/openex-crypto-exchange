package com.openex.backend.service;

import com.openex.backend.dto.MarketHistoryPointResponse;
import com.openex.backend.dto.MarketMetricsResponse;
import com.openex.backend.dto.MarketOverviewResponse;
import com.openex.backend.dto.MarketTickerResponse;
import com.openex.backend.dto.TradeUpdateMessage;
import com.openex.backend.model.Order.OrderStatus;
import com.openex.backend.repository.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class MarketDataService {

    private static final int HISTORY_LIMIT = 36;
    private static final Duration HISTORY_INTERVAL = Duration.ofMinutes(5);

    private final OrderRepository orderRepository;
    private final Map<String, SymbolState> symbolStates = new LinkedHashMap<>();

    public MarketDataService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public synchronized void seedDemoMarketData() {
        if (!symbolStates.isEmpty()) {
            return;
        }

        registerSymbol("BTC/USD", BigDecimal.valueOf(67450), BigDecimal.valueOf(19_710_000));
        registerSymbol("ETH/USD", BigDecimal.valueOf(3480), BigDecimal.valueOf(120_150_000));
        registerSymbol("SOL/USD", BigDecimal.valueOf(168), BigDecimal.valueOf(467_000_000));
        registerSymbol("USDT/USD", BigDecimal.valueOf(1.00), BigDecimal.valueOf(112_000_000_000L));

        Instant start = Instant.now().minus(HISTORY_INTERVAL.multipliedBy(HISTORY_LIMIT));
        for (SymbolState state : symbolStates.values()) {
            BigDecimal seededPrice = state.currentPrice;
            for (int index = 0; index < HISTORY_LIMIT; index++) {
                seededPrice = nextPrice(state, seededPrice, false);
                BigDecimal volume = randomVolume(state.symbol);
                Instant pointTime = start.plus(HISTORY_INTERVAL.multipliedBy(index + 1L));
                state.history.addLast(new TradeUpdateMessage(
                        state.symbol,
                        seededPrice,
                        volume,
                        randomSide(),
                        percentageChange(seededPrice, state.open24hPrice),
                        pointTime
                ));
            }
            if (!state.history.isEmpty()) {
                state.currentPrice = state.history.getLast().price();
            }
        }
    }

    public synchronized TradeUpdateMessage generateNextTrade() {
        seedDemoMarketData();
        List<SymbolState> states = new ArrayList<>(symbolStates.values());
        SymbolState state = states.get(ThreadLocalRandom.current().nextInt(states.size()));

        BigDecimal nextPrice = nextPrice(state, state.currentPrice, true);
        BigDecimal volume = randomVolume(state.symbol);
        TradeUpdateMessage trade = new TradeUpdateMessage(
                state.symbol,
                nextPrice,
                volume,
                randomSide(),
                percentageChange(nextPrice, state.open24hPrice),
                Instant.now()
        );

        state.currentPrice = nextPrice;
        state.history.addLast(trade);
        while (state.history.size() > HISTORY_LIMIT) {
            state.history.removeFirst();
        }
        state.volume24h = state.volume24h.add(volume).setScale(4, RoundingMode.HALF_UP);
        return trade;
    }

    public synchronized MarketOverviewResponse getOverview() {
        seedDemoMarketData();

        List<MarketTickerResponse> tickers = symbolStates.values().stream()
                .map(this::toTicker)
                .sorted(Comparator.comparing(MarketTickerResponse::symbol))
                .toList();

        Map<String, List<MarketHistoryPointResponse>> history = new LinkedHashMap<>();
        for (SymbolState state : symbolStates.values()) {
            history.put(
                    state.symbol,
                    state.history.stream()
                            .map(item -> new MarketHistoryPointResponse(item.timestamp(), item.price(), item.volume()))
                            .toList()
            );
        }

        BigDecimal totalMarketCap = tickers.stream()
                .map(MarketTickerResponse::marketCap)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVolume = tickers.stream()
                .map(MarketTickerResponse::volume24h)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal btcMarketCap = symbolStates.get("BTC/USD").currentPrice
                .multiply(symbolStates.get("BTC/USD").circulatingSupply)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal btcDominance = totalMarketCap.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : btcMarketCap.multiply(BigDecimal.valueOf(100))
                        .divide(totalMarketCap, 2, RoundingMode.HALF_UP);

        long activeTrades = orderRepository.countByStatusIn(EnumSet.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));

        return new MarketOverviewResponse(
                tickers,
                history,
                new MarketMetricsResponse(
                        totalVolume.setScale(2, RoundingMode.HALF_UP),
                        totalMarketCap.setScale(2, RoundingMode.HALF_UP),
                        btcDominance,
                        activeTrades
                )
        );
    }

    private void registerSymbol(String symbol, BigDecimal startPrice, BigDecimal supply) {
        symbolStates.put(symbol, new SymbolState(symbol, startPrice, supply, startPrice));
    }

    private MarketTickerResponse toTicker(SymbolState state) {
        TradeUpdateMessage lastTrade = state.history.peekLast();
        Instant timestamp = lastTrade != null ? lastTrade.timestamp() : Instant.now();
        BigDecimal currentPrice = lastTrade != null ? lastTrade.price() : state.currentPrice;
        return new MarketTickerResponse(
                state.symbol,
                currentPrice.setScale(2, RoundingMode.HALF_UP),
                percentageChange(currentPrice, state.open24hPrice),
                state.volume24h.setScale(2, RoundingMode.HALF_UP),
                currentPrice.multiply(state.circulatingSupply).setScale(2, RoundingMode.HALF_UP),
                timestamp
        );
    }

    private BigDecimal nextPrice(SymbolState state, BigDecimal currentPrice, boolean preserveStable) {
        double volatility;
        if ("BTC/USD".equals(state.symbol)) {
            volatility = 0.0048;
        } else if ("ETH/USD".equals(state.symbol)) {
            volatility = 0.0065;
        } else if ("SOL/USD".equals(state.symbol)) {
            volatility = 0.0105;
        } else {
            volatility = preserveStable ? 0.0009 : 0.0004;
        }

        double drift = ThreadLocalRandom.current().nextDouble(-volatility, volatility);
        BigDecimal multiplier = BigDecimal.valueOf(1 + drift);
        BigDecimal next = currentPrice.multiply(multiplier);

        if ("USDT/USD".equals(state.symbol)) {
            next = next.max(BigDecimal.valueOf(0.997)).min(BigDecimal.valueOf(1.003));
        }

        return next.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal randomVolume(String symbol) {
        double volume;
        if ("BTC/USD".equals(symbol)) {
            volume = ThreadLocalRandom.current().nextDouble(0.35, 6.2);
        } else if ("ETH/USD".equals(symbol)) {
            volume = ThreadLocalRandom.current().nextDouble(2.0, 38.0);
        } else if ("SOL/USD".equals(symbol)) {
            volume = ThreadLocalRandom.current().nextDouble(25.0, 420.0);
        } else {
            volume = ThreadLocalRandom.current().nextDouble(5_000.0, 45_000.0);
        }
        return BigDecimal.valueOf(volume).setScale(4, RoundingMode.HALF_UP);
    }

    private String randomSide() {
        return ThreadLocalRandom.current().nextBoolean() ? "BUY" : "SELL";
    }

    private BigDecimal percentageChange(BigDecimal currentPrice, BigDecimal openPrice) {
        return currentPrice.subtract(openPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(openPrice, 2, RoundingMode.HALF_UP);
    }

    private static final class SymbolState {
        private final String symbol;
        private final BigDecimal circulatingSupply;
        private final BigDecimal open24hPrice;
        private final Deque<TradeUpdateMessage> history = new ArrayDeque<>();
        private BigDecimal currentPrice;
        private BigDecimal volume24h = BigDecimal.ZERO;

        private SymbolState(
                String symbol,
                BigDecimal currentPrice,
                BigDecimal circulatingSupply,
                BigDecimal open24hPrice
        ) {
            this.symbol = symbol;
            this.currentPrice = currentPrice;
            this.circulatingSupply = circulatingSupply;
            this.open24hPrice = open24hPrice;
        }
    }
}

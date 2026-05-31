package com.openex.backend.config;

import com.openex.backend.model.Order;
import com.openex.backend.model.User;
import com.openex.backend.model.Wallet;
import com.openex.backend.repository.OrderRepository;
import com.openex.backend.repository.UserRepository;
import com.openex.backend.repository.WalletRepository;
import com.openex.backend.service.MarketDataService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedOpenExDemoData(
            UserRepository userRepository,
            WalletRepository walletRepository,
            OrderRepository orderRepository,
            MarketDataService marketDataService
    ) {
        return args -> {
            marketDataService.seedDemoMarketData();

            if (userRepository.count() > 0 || walletRepository.count() > 0 || orderRepository.count() > 0) {
                return;
            }

            User ava = userRepository.save(User.builder()
                    .username("ava.trader")
                    .email("ava@openex.demo")
                    .password("demo123")
                    .enabled(true)
                    .build());

            User liam = userRepository.save(User.builder()
                    .username("liam.quant")
                    .email("liam@openex.demo")
                    .password("demo123")
                    .enabled(true)
                    .build());

            User zara = userRepository.save(User.builder()
                    .username("zara.market")
                    .email("zara@openex.demo")
                    .password("demo123")
                    .enabled(true)
                    .build());

            walletRepository.saveAll(List.of(
                    buildWallet(ava, "BTC", "1.24500000", "0.12000000"),
                    buildWallet(ava, "ETH", "18.75000000", "2.30000000"),
                    buildWallet(ava, "USDT", "24500.00000000", "3200.00000000"),
                    buildWallet(liam, "SOL", "640.50000000", "120.00000000"),
                    buildWallet(liam, "USDT", "18500.00000000", "1500.00000000"),
                    buildWallet(liam, "BTC", "0.84500000", "0.05000000"),
                    buildWallet(zara, "ETH", "32.20000000", "4.50000000"),
                    buildWallet(zara, "SOL", "920.00000000", "180.00000000"),
                    buildWallet(zara, "USDT", "41200.00000000", "6100.00000000")
            ));

            orderRepository.saveAll(List.of(
                    buildOrder(ava, Order.OrderSide.BUY, Order.OrderType.LIMIT, Order.OrderStatus.OPEN, "BTC", "USDT", "0.35000000", "0.00000000", "66850.00"),
                    buildOrder(ava, Order.OrderSide.SELL, Order.OrderType.LIMIT, Order.OrderStatus.PARTIALLY_FILLED, "ETH", "USDT", "4.20000000", "1.50000000", "3525.00"),
                    buildOrder(liam, Order.OrderSide.BUY, Order.OrderType.MARKET, Order.OrderStatus.FILLED, "SOL", "USDT", "85.00000000", "85.00000000", null),
                    buildOrder(liam, Order.OrderSide.SELL, Order.OrderType.LIMIT, Order.OrderStatus.OPEN, "BTC", "USDT", "0.12000000", "0.00000000", "67980.00"),
                    buildOrder(zara, Order.OrderSide.BUY, Order.OrderType.LIMIT, Order.OrderStatus.PARTIALLY_FILLED, "ETH", "USDT", "7.50000000", "2.10000000", "3440.00"),
                    buildOrder(zara, Order.OrderSide.SELL, Order.OrderType.LIMIT, Order.OrderStatus.OPEN, "SOL", "USDT", "140.00000000", "0.00000000", "172.40")
            ));
        };
    }

    private Wallet buildWallet(User user, String currency, String balance, String lockedBalance) {
        return Wallet.builder()
                .user(user)
                .currency(currency)
                .balance(new BigDecimal(balance))
                .lockedBalance(new BigDecimal(lockedBalance))
                .build();
    }

    private Order buildOrder(
            User user,
            Order.OrderSide side,
            Order.OrderType type,
            Order.OrderStatus status,
            String baseCurrency,
            String quoteCurrency,
            String quantity,
            String filledQuantity,
            String price
    ) {
        return Order.builder()
                .user(user)
                .side(side)
                .type(type)
                .status(status)
                .baseCurrency(baseCurrency)
                .quoteCurrency(quoteCurrency)
                .quantity(new BigDecimal(quantity))
                .filledQuantity(new BigDecimal(filledQuantity))
                .price(price != null ? new BigDecimal(price) : null)
                .build();
    }
}

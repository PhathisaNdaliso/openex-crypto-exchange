package com.openex.backend.service;

import com.openex.backend.dto.OrderRequest;
import com.openex.backend.dto.OrderResponse;
import com.openex.backend.dto.OrderStreamMessage;
import com.openex.backend.model.Order;
import com.openex.backend.model.User;
import com.openex.backend.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(
            OrderRepository orderRepository,
            UserService userService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    @Cacheable("ordersAll")
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ordersById", key = "#id")
    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrderEntityById(id));
    }

    @CacheEvict(cacheNames = {"ordersAll", "ordersById"}, allEntries = true)
    public OrderResponse createOrder(OrderRequest request) {
        validateOrderRequest(request);
        User user = userService.findUserEntityById(request.userId());

        Order order = Order.builder()
                .user(user)
                .side(request.side())
                .type(request.type())
                .status(request.status() != null ? request.status() : Order.OrderStatus.OPEN)
                .baseCurrency(request.baseCurrency().trim().toUpperCase())
                .quoteCurrency(request.quoteCurrency().trim().toUpperCase())
                .quantity(request.quantity())
                .filledQuantity(defaultIfNull(request.filledQuantity()))
                .price(request.price())
                .build();

        OrderResponse response = toResponse(orderRepository.save(order));
        publishOrderEvent("CREATED", response);
        return response;
    }

    @CacheEvict(cacheNames = {"ordersAll", "ordersById"}, allEntries = true)
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        validateOrderRequest(request);

        Order order = findOrderEntityById(id);
        User user = userService.findUserEntityById(request.userId());

        order.setUser(user);
        order.setSide(request.side());
        order.setType(request.type());
        order.setStatus(request.status() != null ? request.status() : order.getStatus());
        order.setBaseCurrency(request.baseCurrency().trim().toUpperCase());
        order.setQuoteCurrency(request.quoteCurrency().trim().toUpperCase());
        order.setQuantity(request.quantity());
        order.setFilledQuantity(defaultIfNull(request.filledQuantity()));
        order.setPrice(request.price());

        OrderResponse response = toResponse(orderRepository.save(order));
        publishOrderEvent("UPDATED", response);
        return response;
    }

    @CacheEvict(cacheNames = {"ordersAll", "ordersById"}, allEntries = true)
    public void deleteOrder(Long id) {
        Order order = findOrderEntityById(id);
        OrderResponse response = toResponse(order);
        orderRepository.delete(order);
        publishOrderEvent("DELETED", response);
    }

    @Transactional(readOnly = true)
    public Order findOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order payload is required");
        }
        if (request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        if (request.side() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order side is required");
        }
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order type is required");
        }
        if (isBlank(request.baseCurrency()) || isBlank(request.quoteCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trading pair is required");
        }
        if (request.baseCurrency().trim().equalsIgnoreCase(request.quoteCurrency().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base and quote currency must differ");
        }
        if (request.quantity() == null || request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        BigDecimal filledQuantity = defaultIfNull(request.filledQuantity());
        if (filledQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filled quantity cannot be negative");
        }
        if (filledQuantity.compareTo(request.quantity()) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filled quantity cannot exceed order quantity"
            );
        }

        if (request.type() == Order.OrderType.LIMIT) {
            if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Price must be greater than zero for limit orders"
                );
            }
        } else if (request.price() != null && request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
        }
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getSide(),
                order.getType(),
                order.getStatus(),
                order.getBaseCurrency(),
                order.getQuoteCurrency(),
                order.getQuantity(),
                order.getFilledQuantity(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void publishOrderEvent(String eventType, OrderResponse response) {
        OrderStreamMessage streamMessage = new OrderStreamMessage(
                response.id(),
                response.userId(),
                response.status(),
                eventType,
                Instant.now()
        );
        messagingTemplate.convertAndSend("/topic/orders", streamMessage);
        messagingTemplate.convertAndSend("/topic/orders/" + response.userId(), streamMessage);
        messagingTemplate.convertAndSend("/queue/order-events", streamMessage);
    }
}

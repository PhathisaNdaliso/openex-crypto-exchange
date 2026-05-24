package com.openex.backend.service;

import com.openex.backend.dto.OrderRequest;
import com.openex.backend.dto.OrderResponse;
import com.openex.backend.model.Order;
import com.openex.backend.model.User;
import com.openex.backend.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrderEntityById(id));
    }

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

        return toResponse(orderRepository.save(order));
    }

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

        return toResponse(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        orderRepository.deleteById(id);
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
}

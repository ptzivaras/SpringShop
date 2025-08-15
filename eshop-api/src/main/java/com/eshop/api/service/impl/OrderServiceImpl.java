package com.eshop.api.service.impl;

import com.eshop.api.domain.*;
import com.eshop.api.dto.*;
import com.eshop.api.repository.OrderRepository;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.service.OrderService;
import com.eshop.api.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final SecurityUtils securityUtils;

    public OrderServiceImpl(OrderRepository orderRepo,
                            ProductRepository productRepo,
                            UserRepository userRepo,
                            SecurityUtils securityUtils) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMine() {
        Long userId = currentUserId();
        return orderRepo.findAll().stream() // consider add findByUserId(userId)
                .filter(o -> o.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse createForCurrentUser(OrderCreateRequest req) {
        Long userId = currentUserId();

        Order order = Order.builder()
                .userId(userId)
                .orderDate(Instant.now())
                .build();

        List<OrderItem> items = req.getItems().stream()
                .map(itemReq -> {
                    Product product = productRepo.findById(itemReq.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
                    int newStock = product.getStockQuantity() - itemReq.getQuantity();
                    if (newStock < 0) throw new IllegalArgumentException("Out of stock: " + product.getName());
                    product.setStockQuantity(newStock);
                    productRepo.save(product);

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemReq.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        double total = items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepo.save(order);
        return toResponse(saved);
    }

    private Long currentUserId() {
        String username = securityUtils.currentUsername();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    private OrderResponse toResponse(Order o) {
        var itemRes = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .orderDate(o.getOrderDate())
                .totalPrice(o.getTotalPrice())
                .items(itemRes)
                .build();
    }
}

package com.eshop.api.service.impl;

import com.eshop.api.domain.*;
import com.eshop.api.dto.*;
import com.eshop.api.repository.OrderRepository;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderServiceImpl(OrderRepository orderRepo,
                            ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<OrderResponse> getAll() {
        return orderRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse create(OrderRequest req) {
        // Build Order and OrderItems
        Order order = Order.builder()
                .userId(req.getUserId())
                .orderDate(Instant.now())
                .build();

        List<OrderItem> items = req.getItems().stream()
                .map(itemReq -> {
                    Product product = productRepo.findById(itemReq.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

                    // Deduct stock
                    int newStock = product.getStockQuantity() - itemReq.getQuantity();
                    if (newStock < 0) {
                        throw new IllegalArgumentException("Out of stock: " + product.getName());
                    }
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

        // Total price
        double total = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepo.save(order);
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemRes = o.getItems().stream()
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

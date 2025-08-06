package com.eshop.api.service.impl;

import com.eshop.api.domain.*;
import com.eshop.api.dto.*;
import com.eshop.api.repository.*;
import com.eshop.api.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepo;
    private final ProductRepository productRepo;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepo,
                                   ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartResponse getByUserId(Long userId) {
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> ShoppingCart.builder()
                        .userId(userId)
                        .items(Collections.emptyList())
                        .build());

        return toResponse(cart);
    }

    @Override
    @Transactional
    public ShoppingCartResponse addItem(Long userId, CartItemRequest req) {
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> ShoppingCart.builder()
                        .userId(userId)
                        .build());

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Either update existing or add new
        cart.getItems().removeIf(item -> {
            if (item.getProduct().getId().equals(req.getProductId())) {
                item.setQuantity(item.getQuantity() + req.getQuantity());
                return false;
            }
            return false;
        });

        CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(req.getQuantity())
                .build();

        cart.getItems().add(newItem);
        ShoppingCart saved = cartRepo.save(cart);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ShoppingCartResponse removeItem(Long userId, Long productId) {
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        ShoppingCart saved = cartRepo.save(cart);
        return toResponse(saved);
    }

    private ShoppingCartResponse toResponse(ShoppingCart cart) {
        var items = cart.getItems().stream()
                .map(i -> CartItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        return ShoppingCartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .build();
    }
}

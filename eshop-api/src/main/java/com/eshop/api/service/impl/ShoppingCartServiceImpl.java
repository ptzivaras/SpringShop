package com.eshop.api.service.impl;

import com.eshop.api.domain.CartItem;
import com.eshop.api.domain.Product;
import com.eshop.api.domain.ShoppingCart;
import com.eshop.api.dto.CartItemRequest;
import com.eshop.api.dto.CartItemResponse;
import com.eshop.api.dto.ShoppingCartResponse;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.repository.ShoppingCartRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.service.ShoppingCartService;
import com.eshop.api.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final SecurityUtils securityUtils;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepo,
                                   ProductRepository productRepo,
                                   UserRepository userRepo,
                                   SecurityUtils securityUtils) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartResponse getMyCart() {
        Long userId = currentUserId();
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> ShoppingCart.builder()
                        .userId(userId)
                        .items(new ArrayList<>())
                        .build());
        return toResponse(cart);
    }

    @Override
    @Transactional
    public ShoppingCartResponse addItemToMyCart(CartItemRequest req) {
        Long userId = currentUserId();
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> ShoppingCart.builder()
                        .userId(userId)
                        .items(new ArrayList<>())
                        .build());
        if (cart.getItems() == null) cart.setItems(new ArrayList<>());

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // check if already in cart
        var existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(req.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + req.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(req.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        ShoppingCart saved = cartRepo.save(cart);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ShoppingCartResponse removeItemFromMyCart(Long productId) {
        Long userId = currentUserId();
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getItems() != null) {
            cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        }
        ShoppingCart saved = cartRepo.save(cart);
        return toResponse(saved);
    }

    private Long currentUserId() {
        String username = securityUtils.currentUsername();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    private ShoppingCartResponse toResponse(ShoppingCart cart) {
        var items = cart.getItems() == null ? new ArrayList<CartItem>() : cart.getItems();
        var itemDtos = items.stream()
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
                .items(itemDtos)
                .build();
    }
}

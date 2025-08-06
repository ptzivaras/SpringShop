package com.eshop.api.service;

import com.eshop.api.dto.CartItemRequest;
import com.eshop.api.dto.ShoppingCartResponse;

public interface ShoppingCartService {
    ShoppingCartResponse getByUserId(Long userId);
    ShoppingCartResponse addItem(Long userId, CartItemRequest request);
    ShoppingCartResponse removeItem(Long userId, Long productId);
}

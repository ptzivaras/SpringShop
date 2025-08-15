package com.eshop.api.service;

import com.eshop.api.dto.CartAddItemRequest;
import com.eshop.api.dto.ShoppingCartResponse;

public interface ShoppingCartService {
    ShoppingCartResponse getMyCart();
    ShoppingCartResponse addItem(CartAddItemRequest req);
    ShoppingCartResponse updateItem(Long productId, int quantity);
    ShoppingCartResponse removeItem(Long productId);
    ShoppingCartResponse clear();
}

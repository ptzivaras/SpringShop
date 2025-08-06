package com.eshop.api.api;

import com.eshop.api.dto.*;
import com.eshop.api.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ShoppingCartResponse getCart(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @PostMapping("/user/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponse addItem(
            @PathVariable Long userId,
            @Validated @RequestBody CartItemRequest request
    ) {
        return service.addItem(userId, request);
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    public ShoppingCartResponse removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        return service.removeItem(userId, productId);
    }
}

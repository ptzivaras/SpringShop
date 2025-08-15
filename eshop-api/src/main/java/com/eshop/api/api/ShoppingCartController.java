package com.eshop.api.controller;

import com.eshop.api.dto.CartAddItemRequest;
import com.eshop.api.dto.ShoppingCartResponse;
import com.eshop.api.service.ShoppingCartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    @GetMapping
    public ShoppingCartResponse get() { return service.getMyCart(); }

    @PostMapping("/items")
    public ShoppingCartResponse add(@Valid @RequestBody CartAddItemRequest req) {
        return service.addItem(req);
    }

    @PatchMapping("/items/{productId}")
    public ShoppingCartResponse update(@PathVariable Long productId,
                                       @RequestParam int quantity) {
        return service.updateItem(productId, quantity);
    }

    @DeleteMapping("/items/{productId}")
    public ShoppingCartResponse remove(@PathVariable Long productId) {
        return service.removeItem(productId);
    }

    @DeleteMapping
    public ShoppingCartResponse clear() { return service.clear(); }
}

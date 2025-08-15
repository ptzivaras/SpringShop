package com.eshop.api.api;

import com.eshop.api.dto.CartItemRequest;
import com.eshop.api.dto.ShoppingCartResponse;
import com.eshop.api.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCartResponse getMyCart() {
        return service.getMyCart();
    }

    @PostMapping("/me/items")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponse addItem(@Validated @RequestBody CartItemRequest request) {
        return service.addItemToMyCart(request);
    }

    @DeleteMapping("/me/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCartResponse removeItem(@PathVariable Long productId) {
        return service.removeItemFromMyCart(productId);
    }
}

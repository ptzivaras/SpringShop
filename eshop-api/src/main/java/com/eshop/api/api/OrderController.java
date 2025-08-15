package com.eshop.api.api;

import com.eshop.api.dto.OrderCreateRequest;
import com.eshop.api.dto.OrderResponse;
import com.eshop.api.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponse> myOrders() {
        return service.getMine();
    }

    @PostMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createMyOrder(@Validated @RequestBody OrderCreateRequest request) {
        return service.createForCurrentUser(request);
    }

    @PostMapping("/me/checkout")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkoutMyCart() {
        return service.createFromCurrentUserCart();
    }
}

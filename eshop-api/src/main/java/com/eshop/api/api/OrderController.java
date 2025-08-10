package com.eshop.api.api;

import com.eshop.api.dto.OrderRequest;
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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Validated @RequestBody OrderRequest request) {
        return service.create(request);
    }
}

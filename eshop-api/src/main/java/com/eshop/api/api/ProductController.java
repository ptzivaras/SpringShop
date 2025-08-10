package com.eshop.api.api;

import com.eshop.api.dto.ProductRequest;
import com.eshop.api.dto.ProductResponse;
import com.eshop.api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    @PreAuthorize
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Validated @RequestBody ProductRequest request) {
        return service.create(request);
    }
}

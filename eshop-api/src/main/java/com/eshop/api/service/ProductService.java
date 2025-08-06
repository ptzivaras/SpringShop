package com.eshop.api.service;

import com.eshop.api.dto.ProductRequest;
import com.eshop.api.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();
    ProductResponse create(ProductRequest request);
}

package com.eshop.api.service;

import com.eshop.api.dto.product.*;
import org.springframework.data.domain.*;

public interface ProductService {
    Page<ProductResponse> search(String search, Long categoryId, Pageable pageable);
    ProductResponse get(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}

package com.eshop.api.service;

import com.eshop.api.dto.category.*;
import org.springframework.data.domain.*;

public interface CategoryService {
    Page<CategoryResponse> list(Pageable pageable);
    CategoryResponse get(Long id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}

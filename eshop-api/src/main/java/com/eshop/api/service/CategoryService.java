package com.eshop.api.service;

import com.eshop.api.dto.CategoryRequest;
import com.eshop.api.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();
    CategoryResponse create(CategoryRequest request);
}

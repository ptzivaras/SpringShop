package com.eshop.api.service.impl;

import com.eshop.api.domain.Category;
import com.eshop.api.dto.CategoryRequest;
import com.eshop.api.dto.CategoryResponse;
import com.eshop.api.repository.CategoryRepository;
import com.eshop.api.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CategoryResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        Category cat = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        Category saved = repo.save(cat);
        return toResponse(saved);
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}

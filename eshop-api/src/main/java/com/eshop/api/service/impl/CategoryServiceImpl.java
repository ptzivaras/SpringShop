package com.eshop.api.service.impl;

import com.eshop.api.domain.Category;
import com.eshop.api.dto.CategoryRequest;
import com.eshop.api.dto.CategoryResponse;
import com.eshop.api.exception.NotFoundException;
import com.eshop.api.repository.CategoryRepository;
import com.eshop.api.service.CategoryService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) { this.repo = repo; }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        return repo.findAll(pageable)
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        Category c = repo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category c = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        c = repo.save(c);
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category c = repo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        c.setName(request.name());
        c.setDescription(request.description());
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Category not found");
        repo.deleteById(id);
    }
}

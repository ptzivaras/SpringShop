package com.eshop.api.service.impl;

import com.eshop.api.domain.*;
import com.eshop.api.dto.product.*;
import com.eshop.api.exception.NotFoundException;
import com.eshop.api.repository.*;
import com.eshop.api.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String search, Long categoryId, Pageable pageable) {
        return productRepo.search(blankToNull(search), categoryId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product p = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return toResponse(p);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Category cat = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Product p = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(cat)
                .build();
        p = productRepo.save(p);
        return toResponse(p);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product p = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        Category cat = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setStock(request.stock());
        p.setCategory(cat);
        return toResponse(p);
    }

    @Override
    public void delete(Long id) {
        if (!productRepo.existsById(id)) throw new NotFoundException("Product not found");
        productRepo.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getStock(),
                p.getCategory().getId(),
                p.getCategory().getName()
        );
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}

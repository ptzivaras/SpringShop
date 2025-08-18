package com.eshop.api.service.impl;

import com.eshop.api.domain.Category;
import com.eshop.api.domain.Product;
import com.eshop.api.dto.ProductResponse;
import com.eshop.api.dto.ProductRequest;
import com.eshop.api.exception.NotFoundException;
import com.eshop.api.repository.CategoryRepository;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    // ----------------- QUERY -----------------
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String search, Long categoryId, Pageable pageable) {
        return productRepo.search(blankToNull(search), categoryId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return toResponse(p);
    }

    // ----------------- MUTATIONS -----------------
    @Override
    public ProductResponse create(ProductRequest request) {
        Category cat = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product p = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(cat)
                .build();

        p = productRepo.save(p);
        return toResponse(p);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Category cat = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setStock(request.getStock());
        p.setCategory(cat);

        p = productRepo.save(p);
        return toResponse(p);
    }

    @Override
    public void delete(Long id) {
        if (!productRepo.existsById(id)) {
            throw new NotFoundException("Product not found");
        }
        productRepo.deleteById(id);
    }

    // ----------------- IMAGE UPLOAD -----------------
    @Override
    public ProductResponse uploadImage(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new IllegalArgumentException("Only image/* allowed");
        }

        Product p = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        try {
            Files.createDirectories(Path.of(uploadDir));

            String ext = switch (ct) {
                case "image/png"  -> ".png";
                case "image/jpeg" -> ".jpg";
                case "image/webp" -> ".webp";
                default -> ".img";
            };

            String filename = "product-" + id + "-" + UUID.randomUUID() + ext;
            Path dest = Path.of(uploadDir, filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            // προβάλλουμε με /uploads/**
            p.setImageUrl("/uploads/" + filename);
            p = productRepo.save(p);

            return toResponse(p);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // ----------------- MAPPERS -----------------
    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .imageUrl(p.getImageUrl())
                .build();
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}

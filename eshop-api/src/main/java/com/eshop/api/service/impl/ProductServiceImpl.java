package com.eshop.api.service.impl;

import com.eshop.api.domain.Product;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Product> getAll() {
        return repo.findAll();
    }
}

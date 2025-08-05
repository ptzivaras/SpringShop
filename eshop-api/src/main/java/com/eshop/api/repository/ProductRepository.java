package com.eshop.api.repository;

import com.eshop.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { //primary key is type long
}

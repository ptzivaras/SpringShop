package com.eshop.api.repository;

import com.eshop.api.domain.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
           select p from Product p
           where (:search is null or lower(p.name) like lower(concat('%', :search, '%'))
                  or lower(p.description) like lower(concat('%', :search, '%')))
             and (:categoryId is null or p.category.id = :categoryId)
           """)
    Page<Product> search(@Param("search") String search,
                         @Param("categoryId") Long categoryId,
                         Pageable pageable);
}

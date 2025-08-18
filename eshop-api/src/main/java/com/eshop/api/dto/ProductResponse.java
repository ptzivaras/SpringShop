package com.eshop.api.dto;

import java.math.BigDecimal;
import lombok.*;

/*
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        Long categoryId,
        String categoryName,
        String imageUrl
) {}
*/

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private Long categoryId;
    private String categoryName;
    private String imageUrl; // κράτα το, το χρειάζεται το upload
}
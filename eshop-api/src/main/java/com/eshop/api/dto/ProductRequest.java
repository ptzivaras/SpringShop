package com.eshop.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/*
public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @Min(0) int stock,
        @NotNull Long categoryId
) {}
*/

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;

    @Min(0)
    private int stock;

    @NotNull
    private Long categoryId;
}
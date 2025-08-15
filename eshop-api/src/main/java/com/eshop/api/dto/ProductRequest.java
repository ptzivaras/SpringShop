package com.eshop.api.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @Min(0) int stock,
        @NotNull Long categoryId
) {}

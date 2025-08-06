package com.eshop.api.dto;

import lombok.*;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @PositiveOrZero
    private Double price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    // TODO: is it needed for link or not?
    private Long categoryId;
}

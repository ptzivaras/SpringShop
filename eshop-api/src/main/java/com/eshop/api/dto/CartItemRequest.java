package com.eshop.api.dto;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {
    @NotNull
    private Long productId;

    @NotNull @Min(1)
    private Integer quantity;
}

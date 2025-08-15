package com.eshop.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CartAddItemRequest {
    @NotNull private Long productId;
    @Min(1)  private int quantity;
}

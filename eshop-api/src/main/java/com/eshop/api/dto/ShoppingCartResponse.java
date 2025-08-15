package com.eshop.api.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ShoppingCartResponse {
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal subtotal;
}

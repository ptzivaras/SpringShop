package com.eshop.api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
}

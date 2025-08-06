package com.eshop.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
}

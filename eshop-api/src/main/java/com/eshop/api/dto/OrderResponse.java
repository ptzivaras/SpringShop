package com.eshop.api.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private Instant orderDate;
    private Double totalPrice;
    private List<OrderItemResponse> items;
}

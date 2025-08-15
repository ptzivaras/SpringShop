package com.eshop.api.dto;

import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {
    @NotEmpty
    private List<OrderItemRequest> items;
}

package com.eshop.api.service;

import com.eshop.api.dto.OrderRequest;
import com.eshop.api.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll();
    OrderResponse create(OrderRequest request);
}

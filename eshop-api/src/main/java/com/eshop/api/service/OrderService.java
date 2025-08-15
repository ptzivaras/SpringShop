package com.eshop.api.service;

import com.eshop.api.dto.*;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getMine();
    OrderResponse createForCurrentUser(OrderCreateRequest request);
    // ftiaxnei order apo to kalathi tou logged iin user
    OrderResponse createFromCurrentUserCart();
}

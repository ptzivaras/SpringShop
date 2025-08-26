package com.eshop.api.service.impl;

import com.eshop.api.domain.Order;
import com.eshop.api.domain.OrderItem;
import com.eshop.api.domain.Product;
import com.eshop.api.domain.ShoppingCart;
import com.eshop.api.domain.CartItem;
import com.eshop.api.dto.OrderCreateRequest;
import com.eshop.api.dto.OrderItemResponse;
import com.eshop.api.dto.OrderResponse;
import com.eshop.api.repository.OrderRepository;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.repository.ShoppingCartRepository;
import com.eshop.api.service.OrderService;
import com.eshop.api.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ShoppingCartRepository cartRepo;
    private final SecurityUtils securityUtils;

    public OrderServiceImpl(OrderRepository orderRepo,
                            ProductRepository productRepo,
                            UserRepository userRepo,
                            ShoppingCartRepository cartRepo,
                            SecurityUtils securityUtils) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMine() {
        Long userId = currentUserId();
        return orderRepo.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse checkoutMyCart() {
        Long userId = currentUserId();

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Το καλάθι δεν υπάρχει"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Το καλάθι είναι άδειο");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderDate(Instant.now())
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            int qty = ci.getQuantity();

            if (product.getStock() < qty) {
                throw new IllegalArgumentException("Μη επαρκές stock για: " + product.getName());
            }
            product.setStock(product.getStock() - qty);   // ενημέρωση στοκ
            productRepo.save(product);

            var oi = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(product.getPrice().doubleValue()) // domain OrderItem έχει Double
                    .build();

            order.getItems().add(oi);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        order.setTotalPrice(total.doubleValue());

        // άδειασμα καλαθιού
        cart.getItems().clear();
        cartRepo.save(cart);

        Order saved = orderRepo.save(order);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse createFromCurrentUserCart() {
        Long userId = currentUserId();
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderDate(Instant.now())
                .build();

        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            int qty = ci.getQuantity();

            // Refresh product from DB (προαιρετικό)
            product = productRepo.findById(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

            int newStock = product.getStock() - qty;
            if (newStock < 0) {
                throw new IllegalArgumentException("Out of stock: " + product.getName());
            }
            product.setStock(newStock);
            productRepo.save(product);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(product.getPrice().doubleValue())
                    .build();

            items.add(oi);
        }

        double total = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepo.save(order);

        // Άδειασμα cart (λόγω orphanRemoval=true αρκεί να αδειάσεις τη λίστα)
        cart.getItems().clear();
        cartRepo.save(cart);

        return toResponse(saved);
    }

    private Long currentUserId() {
        String username = securityUtils.currentUsername();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    private OrderResponse toResponse(Order o) {
        var itemRes = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .orderDate(o.getOrderDate())
                .totalPrice(o.getTotalPrice())
                .items(itemRes)
                .build();
    }
}

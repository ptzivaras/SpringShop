package com.eshop.api.service.impl;

import com.eshop.api.domain.CartItem;
import com.eshop.api.domain.Product;
import com.eshop.api.domain.ShoppingCart;
import com.eshop.api.domain.User;
import com.eshop.api.dto.CartAddItemRequest;
import com.eshop.api.dto.CartItemResponse;
import com.eshop.api.dto.ShoppingCartResponse;
import com.eshop.api.repository.CartItemRepository;
import com.eshop.api.repository.ProductRepository;
import com.eshop.api.repository.ShoppingCartRepository;
import com.eshop.api.repository.UserRepository;
import com.eshop.api.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShoppingCartServiceImpl implements com.eshop.api.service.ShoppingCartService {

    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final SecurityUtils securityUtils;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepo,
                                   CartItemRepository cartItemRepo,
                                   ProductRepository productRepo,
                                   UserRepository userRepo,
                                   SecurityUtils securityUtils) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.securityUtils = securityUtils;
    }

    @Override
    public ShoppingCartResponse getMyCart() {
        ShoppingCart cart = getOrCreate();
        return toResponse(cart);
    }

    @Override
    public ShoppingCartResponse addItem(CartAddItemRequest req) {
        ShoppingCart cart = getOrCreate();
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem existing = cart.getItems() == null ? null :
                cart.getItems().stream()
                        .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                        .findFirst().orElse(null);

        if (existing == null) {
            if (cart.getItems() == null) cart.setItems(new ArrayList<>());
            CartItem ci = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(req.getQuantity())
                    .build();
            cart.getItems().add(ci);
        } else {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
        }

        return toResponse(cart);
    }

    @Override
    public ShoppingCartResponse updateItem(Long productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be >= 1");
        ShoppingCart cart = getOrCreate();
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Item not in cart"));
        item.setQuantity(quantity);
        return toResponse(cart);
    }

    @Override
    public ShoppingCartResponse removeItem(Long productId) {
        ShoppingCart cart = getOrCreate();
        if (cart.getItems() != null) {
            cart.getItems().removeIf(ci -> ci.getProduct().getId().equals(productId));
        }
        return toResponse(cart);
    }

    @Override
    public ShoppingCartResponse clear() {
        ShoppingCart cart = getOrCreate();
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        return toResponse(cart);
    }

    // helpers
    private ShoppingCart getOrCreate() {
        Long userId = currentUserId();
        return cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart c = ShoppingCart.builder()
                            .userId(userId)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepo.save(c);
                });
    }

    private Long currentUserId() {
        String username = securityUtils.currentUsername();
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return u.getId();
    }

    private ShoppingCartResponse toResponse(ShoppingCart cart) {
        List<CartItemResponse> items = cart.getItems() == null ? List.of() :
                cart.getItems().stream().map(ci -> {
                    var p = ci.getProduct();
                    BigDecimal unitPrice = p.getPrice(); // Product.price είναι BigDecimal στο δικό σου μοντέλο
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));

                    return CartItemResponse.builder()
                            .productId(p.getId())
                            .productName(p.getName())
                            .unitPrice(unitPrice)
                            .quantity(ci.getQuantity())
                            .lineTotal(lineTotal)
                            .build();
                }).toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        int totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();


        return ShoppingCartResponse.builder()
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
    }
}

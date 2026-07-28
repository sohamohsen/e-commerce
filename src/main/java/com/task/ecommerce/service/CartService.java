package com.task.ecommerce.service;

import com.task.ecommerce.entity.CartItem;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.CartItemRepository;
import com.task.ecommerce.repository.ProductRepository;
import com.task.ecommerce.user.dto.AddToCartRequest;
import com.task.ecommerce.user.dto.CartItemResponse;
import com.task.ecommerce.user.dto.CartResponse;
import com.task.ecommerce.user.dto.UpdateCartItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    @Transactional
    public void addToCart(Integer userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not available now."));

        if (!product.isActive() || product.getQuantity() <= 0) {
            throw new BadRequestException("Product not available now.");
        }

        CartItem cartItem = cartItemRepository
                .findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        int newTotalQuantity = (cartItem != null ? cartItem.getQuantity() : 0) + request.getQuantity();

        if (newTotalQuantity > product.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock. Available: " + product.getQuantity());
        }

        if (cartItem != null) {
            cartItem.setQuantity(newTotalQuantity);
        } else {
            cartItem = CartItem.builder()
                    .userId(userId)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
        }

        cartItemRepository.save(cartItem);
    }

    public CartResponse getCart(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        List<CartItemResponse> items = cartItems.stream()
                .map(ci -> {
                    Product product = productRepository.findById(ci.getProductId())
                            .orElseThrow(() -> new BadRequestException("Product not available"));
                    BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));

                    return CartItemResponse.builder()
                            .id(ci.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .unitPrice(product.getPrice())
                            .quantity(ci.getQuantity())
                            .subtotal(subTotal)
                            .build();
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalAmount(total)
                .build();
    }

    @Transactional
    public void updateCartItem(Integer userId, Integer cartItemId, @Valid UpdateCartItemRequest request) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BadRequestException("Cart item not found."));

        if(!cartItem.getUserId().equals(userId)){
            throw new BadRequestException("Cart item not found.");
        }

        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found."));

        if (request.getQuantity() > product.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock. Available: " + product.getQuantity());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

    }

    @Transactional
    public void removeCartItem(Integer userId, Integer cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BadRequestException("Cart item not found."));

        if (!cartItem.getUserId().equals(userId)) {
            throw new BadRequestException("Cart item not found.");
        }

        cartItemRepository.delete(cartItem);
    }
}

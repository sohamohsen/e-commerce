package com.task.ecommerce.user;

import com.task.ecommerce.entity.User;
import com.task.ecommerce.service.CartService;
import com.task.ecommerce.user.dto.AddToCartRequest;
import com.task.ecommerce.user.dto.CartResponse;
import com.task.ecommerce.user.dto.UpdateCartItemRequest;
import com.task.ecommerce.utils.ReturnObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(
            @RequestBody @Valid AddToCartRequest request,
            @AuthenticationPrincipal User user
            ) {
        cartService.addToCart(user.getId(), request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Product added to cart.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<?> getCart(
            @AuthenticationPrincipal User user
    ){
        CartResponse cart = cartService.getCart(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Cart fetched successfully.")
                .data(cart)
                .build();

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Integer cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        cartService.updateCartItem(user.getId(), cartItemId, request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Cart item updated.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Integer cartItemId,
            @AuthenticationPrincipal User user
    ) {
        cartService.removeCartItem(user.getId(), cartItemId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Item removed from cart.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}

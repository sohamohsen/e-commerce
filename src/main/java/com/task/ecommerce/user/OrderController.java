package com.task.ecommerce.user;

import com.task.ecommerce.admin.dto.OrderDetailResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.service.OrderService;
import com.task.ecommerce.service.dto.OrderSummaryResponse;
import com.task.ecommerce.user.dto.OrderResponse;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Orders (Customer)", description = "Customer endpoints for checking out and viewing orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Checkout cart to create order", description = "Converts items in the customer's cart into a placed order.")
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal User user) {
        OrderResponse order = orderService.checkout(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Order created successfully.")
                .data(order)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get customer orders", description = "Fetch a paginated list of orders placed by the current customer.")
    @GetMapping
    public ResponseEntity<?> getCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal User user
    ) {
        PageResponse<OrderSummaryResponse> orders = orderService.getAllOrders(page, size, status, user);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Orders fetched successfully.")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get order details", description = "Fetch detailed information for a specific order owned by the customer.")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal User user
    ) {
        OrderDetailResponse orders = orderService.getOrderDetails(orderId, user);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Order fetched successfully.")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }
}

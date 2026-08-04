package com.task.ecommerce.user;

import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.service.ProductService;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "Products (Public)", description = "Public endpoints for browsing products and product details")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get paginated products", description = "Fetch products with optional filters for category, name, price range, and sorting.")
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<ProductResponse> products = productService.getPublicProducts(
                page, size, categoryId, name, minPrice, maxPrice, sortBy, sortDir
        );

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Products fetched successfully.")
                .data(products)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product details by ID", description = "Fetch detailed information for a specific product.")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Integer productId) {
        ProductResponse product = productService.getPublicProduct(productId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product fetched successfully.")
                .data(product)
                .build();

        return ResponseEntity.ok(response);
    }
}

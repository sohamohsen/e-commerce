package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.ProductRequest;
import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.admin.dto.StockUpdateRequest;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.service.ProductService;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin-products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<?> addProduct(
            @RequestPart @Valid ProductRequest request,
            @RequestPart(required = false) MultipartFile image,
            @AuthenticationPrincipal User user
    ){
        productService.addProduct(request, image, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Product created successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(
            @PathVariable Integer productId
    ){
        ProductResponse productResponse = productService.getProduct(productId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product caught successfully.")
                .data(productResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer productId,
            @RequestPart @Valid ProductRequest request,
            @RequestPart(required = false) MultipartFile image,
            @AuthenticationPrincipal User user
    ){
        productService.updateProduct(productId, request, image, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product Updated successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<?> updateProductStock(
            @PathVariable Integer productId,
            @RequestBody @Valid StockUpdateRequest request,
            @AuthenticationPrincipal User user
    ){
        productService.updateProductStock(productId, request, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product stock Updated successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable Integer productId,
            @AuthenticationPrincipal User user
    ){
        productService.updateProductStatus(productId, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product status Updated successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Integer productId
    ){
        productService.deleteProduct(productId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product deleted successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir

    ) {
        PageResponse<ProductResponse> products = productService.getProducts(page, size, categoryId, isActive, name, minPrice, maxPrice, maxQuantity, sortBy, sortDir);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Products caught successfully.")
                .data(products)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
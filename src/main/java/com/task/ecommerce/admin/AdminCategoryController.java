package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.CategoryRequest;
import com.task.ecommerce.admin.dto.CategoryResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.service.CategoryService;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin-categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Categories (Admin)", description = "Admin management of product categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category", description = "Adds a new product category.")
    @PostMapping("")
    public ResponseEntity<?> addCategory(
            @RequestBody @Valid CategoryRequest request,
            @AuthenticationPrincipal User user
    ){
        categoryService.addCategory(request, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Category created successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get category by ID", description = "Retrieves specific category details for admin.")
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategory(
            @PathVariable Integer categoryId
    ){
        CategoryResponse categoryResponse = categoryService.getCategory(categoryId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Category caught successfully.")
                .data(categoryResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Update category", description = "Updates details of an existing category.")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody @Valid CategoryRequest request,
            @AuthenticationPrincipal User user
    ){
        categoryService.updateCategory(categoryId, request, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Category Updated successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Delete category", description = "Super Admin only endpoint to remove a category.")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Integer categoryId
    ){
        categoryService.deleteCategory(categoryId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Category deleted successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get categories (Admin)", description = "Fetch paginated list of categories including inactive ones for management.")
    @GetMapping
    public ResponseEntity<?> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<CategoryResponse> categories = categoryService.getCategories(page, size);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Categories caught successfully.")
                .data(categories)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
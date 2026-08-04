package com.task.ecommerce.user;

import com.task.ecommerce.admin.dto.CategoryResponse;
import com.task.ecommerce.service.CategoryService;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "Categories (Public)", description = "Public endpoints for exploring product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get paginated categories", description = "Fetch a list of active product categories.")
    @GetMapping("")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        PageResponse<CategoryResponse> categories = categoryService.getAllCategories(page, size);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Categories fetched successfully.")
                .data(categories)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get category by ID", description = "Fetch detailed information for a specific product category.")
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getPublicCategory(
            @PathVariable Integer categoryId
    ){
        CategoryResponse categoryResponse = categoryService.getPublicCategory(categoryId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Category caught successfully.")
                .data(categoryResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

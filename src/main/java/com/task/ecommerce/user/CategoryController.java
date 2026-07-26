package com.task.ecommerce.user;

import com.task.ecommerce.admin.dto.CategoryResponse;
import com.task.ecommerce.service.CategoryService;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

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

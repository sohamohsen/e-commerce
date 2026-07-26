package com.task.ecommerce.security;

import com.task.ecommerce.admin.dto.CategoryRequest;
import com.task.ecommerce.admin.dto.CategoryResponse;
import com.task.ecommerce.entity.Category;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.CategoryRepository;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public void addCategory(CategoryRequest request, Integer userId) {
        
        if (categoryRepository.existsByName(request.getName())){
            throw new BadRequestException("There is category with same name already exist.");
        }

        Category category = Category.builder()
                .name(request.getName())
                .createdBy(userId)
                .build();
        
        categoryRepository.save(category);
    }

    public void updateCategory(Integer categoryId, CategoryRequest request, Integer userId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category not found."));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("There is a category with the same name already.");
        }

        category.setName(request.getName());
        category.setUpdatedBy(userId);

        categoryRepository.save(category);
    }


    public void deleteCategory(Integer categoryId) {

        categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category not found."));

        if(categoryRepository.hasProducts(categoryId)){
            throw new BadRequestException("There are products with thia category");
        }

        categoryRepository.deleteById(categoryId);
    }

    public CategoryResponse getCategory(Integer categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category not found."));

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .build();
    }

    public PageResponse<CategoryResponse> getCategories(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Category> categories = categoryRepository.findAll(pageable);

        List<CategoryResponse> items = categories.getContent()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt())
                        .updatedAt(category.getUpdatedAt())
                        .createdBy(category.getCreatedBy())
                        .updatedBy(category.getUpdatedBy())
                        .build())
                .toList();

        return PageResponse.<CategoryResponse>builder()
                .items(items)
                .page(categories.getNumber())
                .size(categories.getSize())
                .totalElements(categories.getTotalElements())
                .totalPages(categories.getTotalPages())
                .first(categories.isFirst())
                .last(categories.isLast())
                .build();
    }
}

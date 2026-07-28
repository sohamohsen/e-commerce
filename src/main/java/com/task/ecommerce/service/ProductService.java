package com.task.ecommerce.service;

import com.task.ecommerce.admin.dto.ProductRequest;
import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.admin.dto.StockUpdateRequest;
import com.task.ecommerce.entity.Category;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.CategoryRepository;
import com.task.ecommerce.repository.ProductRepository;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS =
            java.util.Set.of("name", "price", "quantity", "createdAt", "updatedAt");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    public void addProduct(ProductRequest request, MultipartFile image, Integer userId) {

        if(!categoryRepository.existsById(request.getCategoryId())){
            throw new BadRequestException("Wrong category");
        }

        String imageUrl = image != null ? s3Service.uploadProductImage(image) : null;


        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(imageUrl)
                .quantity(request.getQuantity())
                .categoryId(request.getCategoryId())
                .createdBy(userId)
                .isActive(true)
                .build();

        productRepository.save(product);

    }

    public ProductResponse getProduct(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found."));
        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found."));

        return toProductResponse(product, category);
    }

    public void updateProduct(Integer productId, ProductRequest request, MultipartFile newImage, Integer userId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found."));

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new BadRequestException("Category not found.");
        }

        if (newImage != null && !newImage.isEmpty()) {
            s3Service.deleteProductImage(product.getImageUrl());
            product.setImageUrl(s3Service.uploadProductImage(newImage));
        }

        if (request.getCategoryId()!= null) {
            product.setCategoryId(request.getCategoryId());
        }

        if (request.getName()!= null) {
            product.setName(request.getName());
        }

        if (request.getDescription()!= null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice()!= null) {
            product.setPrice(request.getPrice());
        }

        if (request.getQuantity()!= null) {
            product.setUpdatedBy(userId);
        }

        productRepository.save(product);
    }

    public void updateProductStock(Integer productId, StockUpdateRequest request, Integer userId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found."));

        int newQuantity = product.getQuantity() + request.getQuantityChange();
        if (newQuantity < 0) {
            throw new BadRequestException("Insufficient stock. Current quantity: " + product.getQuantity());
        }

        product.setQuantity(newQuantity);
        product.setUpdatedBy(userId);

        productRepository.save(product);
    }

    public void updateProductStatus(Integer productId, Integer userId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found."));

        product.setActive(!product.isActive());
        product.setUpdatedBy(userId);

        productRepository.save(product);
    }

    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequestException("Product not found."));
        s3Service.deleteProductImage(product.getImageUrl());

        productRepository.deleteById(productId);

    }

    public PageResponse<ProductResponse> getProducts(int page, int size, Integer categoryId, Boolean isActive, String name, BigDecimal minPrice, BigDecimal maxPrice, Integer maxQuantity, String sortBy, String sortDir) {

        Pageable pageable = createPageRequest(page, size, sortBy, sortDir, Sort.Direction.DESC);

        Page<ProductResponse> products = productRepository.findProducts(categoryId, isActive, name, minPrice, maxPrice, maxQuantity, pageable);

        return PageResponse.<ProductResponse>builder()
                .items(products.getContent())
                .page(products.getNumber())
                .size(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .first(products.isFirst())
                .last(products.isLast())
                .build();
    }

    public ProductResponse getPublicProduct(Integer productId) {
        ProductResponse product = getProduct(productId);

        if (!product.isActive() || product.getQuantity() <= 0) {
            throw new BadRequestException("Product not found.");
        }
        return product;
    }

    public PageResponse<ProductResponse> getPublicProducts(int page, int size, Integer categoryId, String name, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, String sortDir) {
        Pageable pageable = createPageRequest(page, size, sortBy, sortDir, Sort.Direction.ASC);

        Page<ProductResponse> products = productRepository.findProducts(
                categoryId, true, name, minPrice, maxPrice, null, pageable
        );

        return PageResponse.<ProductResponse>builder()
                .items(products.getContent())
                .page(products.getNumber())
                .size(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .first(products.isFirst())
                .last(products.isLast())
                .build();

    }

    private Pageable createPageRequest(
            int page, int size, String sortBy, String sortDir, Sort.Direction defaultDirection) {

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "name";
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir).orElse(defaultDirection);
        return PageRequest.of(page, size, Sort.by(direction, safeSortBy));
    }

    private ProductResponse toProductResponse(Product product, Category category) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .isActive(product.isActive())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .build();
    }
}

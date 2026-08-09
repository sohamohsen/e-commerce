package com.task.ecommerce.repository;

import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
        SELECT new com.task.ecommerce.admin.dto.ProductResponse(
            p.id,
            p.categoryId,
            c.name,
            p.name,
            p.description,
            p.price,
            p.quantity,
            p.isActive,
            p.imageUrl,
            p.createdAt,
            p.updatedAt,
            p.createdBy,
            p.updatedBy
        )
        FROM Product p
        JOIN Category c ON c.id = p.categoryId
        WHERE (:categoryId IS NULL OR p.categoryId = :categoryId)
          AND (:isActive IS NULL OR p.isActive = :isActive)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:maxQuantity IS NULL OR p.quantity <= :maxQuantity)
        """)
    Page<ProductResponse> findProducts(
            Integer categoryId,
            Boolean isActive,
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer maxQuantity,
            Pageable pageable
    );

    List<Product> findTop30ByIsActiveTrueOrderByCreatedAtDesc();
}
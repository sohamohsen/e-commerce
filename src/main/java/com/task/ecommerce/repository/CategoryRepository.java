package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT COUNT(c) > 0
    FROM Category c
    JOIN Product p ON p.categoryId = c.id
    WHERE c.id = :categoryId
    """)
    boolean hasProducts(@Param("categoryId") Integer categoryId);
}

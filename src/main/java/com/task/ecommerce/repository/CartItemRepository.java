package com.task.ecommerce.repository;

import com.task.ecommerce.entity.CartItem;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);

    List<CartItem> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}

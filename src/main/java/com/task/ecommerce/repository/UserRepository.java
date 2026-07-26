package com.task.ecommerce.repository;

import com.task.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String identifier);

    Optional<User> findByEmailOrPhone(String email, String phone);
}

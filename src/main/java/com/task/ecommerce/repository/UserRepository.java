package com.task.ecommerce.repository;

import com.task.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String identifier);

    Optional<User> findByEmailOrPhone(String email, String phone);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.role = com.task.ecommerce.entity.enums.Role.ADMIN
          AND (:search IS NULL OR
               LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
               u.phone LIKE CONCAT('%', :search, '%'))
          AND (:enabled IS NULL OR u.enabled = :enabled)
          AND (:accountLocked IS NULL OR u.accountLocked = :accountLocked)
          AND (:passwordChanged IS NULL OR u.passwordChanged = :passwordChanged)
          AND (:createdFrom IS NULL OR u.createdAt >= :createdFrom)
          AND (:createdTo IS NULL OR u.createdAt <= :createdTo)
        """)
    Page<User> findAdmins(
            @Param("search") String search,
            @Param("enabled") Boolean enabled,
            @Param("accountLocked") Boolean accountLocked,
            @Param("passwordChanged") Boolean passwordChanged,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            Pageable pageable
    );
}

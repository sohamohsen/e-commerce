package com.task.ecommerce.SuperAdmin;

import com.task.ecommerce.SuperAdmin.dto.AdminAccount;
import com.task.ecommerce.SuperAdmin.dto.CreateAdminAccount;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.ReturnObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/admin")
    public ResponseEntity<ReturnObject> createAdminAccount(
            @RequestBody @Valid CreateAdminAccount request,
            @AuthenticationPrincipal User superAdmin
    ) {

        AdminAccount adminAccount = superAdminService.createAdmin(request, superAdmin.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Admin account created successfully.")
                .data(adminAccount)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/lock-account/{userId}")
    public ResponseEntity<ReturnObject> lockAccount(
            @PathVariable Integer userId,
            @AuthenticationPrincipal User superAdmin
    ) {
        superAdminService.toggleLockAccount(userId, superAdmin.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Account lock toggled successfully.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
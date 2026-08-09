package com.task.ecommerce.SuperAdmin;

import com.task.ecommerce.SuperAdmin.dto.AdminAccount;
import com.task.ecommerce.SuperAdmin.dto.AdminResponse;
import com.task.ecommerce.SuperAdmin.dto.CreateAdminAccount;
import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin Management", description = "Privileged endpoints for managing admin accounts and locking/unlocking user accounts")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @Operation(summary = "Create admin account", description = "Allows Super Admin to provision a new admin user.")
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

    @Operation(summary = "Toggle account lock status", description = "Locks or unlocks a user/admin account.")
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

    @Operation(summary = "Reset admin password", description = "Change admin user by another one in case of forget it")
    @PostMapping("/reset-Password/{userId}")
    public ResponseEntity<ReturnObject> ChangeAdminPassword(
            @PathVariable Integer userId,
            @AuthenticationPrincipal User superAdmin
    ) {
        String Password = superAdminService.ChangeAdminPassword(userId, superAdmin.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Password reset successfully.")
                .data(Password)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get paginated admins",
            description = "Fetch admin accounts with optional filters and sorting."
    )
    @GetMapping("/admins")
    public ResponseEntity<ReturnObject> getAllAdmins(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,

            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Boolean accountLocked,
            @RequestParam(required = false) Boolean passwordChanged,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime createdFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime createdTo
    ) {

        PageResponse<AdminResponse> admins =
                superAdminService.getAllAdmins(
                        page,
                        size,
                        sortBy,
                        sortDir,
                        search,
                        enabled,
                        accountLocked,
                        passwordChanged,
                        createdFrom,
                        createdTo
                );

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Admins fetched successfully.")
                .data(admins)
                .build();

        return ResponseEntity.ok(response);
    }
}
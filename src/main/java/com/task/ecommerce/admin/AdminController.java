package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.ActivateAccount;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin account operations and session management")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final AdminPresenceService adminPresenceService;

    @Operation(summary = "Activate admin account", description = "Activates an admin account using password set during onboarding.")
    @PostMapping("/enable")
    public ResponseEntity<?> activateAccount(
            @RequestBody @Valid ActivateAccount request,
            @AuthenticationPrincipal User user
            ){
        adminService.activateAccount(request, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Account activate successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Admin logout", description = "Logs out the admin and updates presence status to unavailable.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader HttpServletRequest request,
            @AuthenticationPrincipal User user
    ){
        userService.logout(request);
        adminPresenceService.makeUnavailable(user.getId());
        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("logout successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
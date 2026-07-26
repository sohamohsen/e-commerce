package com.task.ecommerce.admin;

import com.task.ecommerce.Auth.dto.LoginResponse;
import com.task.ecommerce.admin.dto.ActivateAccount;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.ReturnObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

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
}
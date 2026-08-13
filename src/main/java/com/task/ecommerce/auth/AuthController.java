package com.task.ecommerce.auth;

import com.task.ecommerce.auth.dto.AdminLoginRequest;
import com.task.ecommerce.auth.dto.LoginRequest;
import com.task.ecommerce.auth.dto.LoginResponse;
import com.task.ecommerce.auth.dto.RegistrationRequest;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for customer registration, customer login, and admin login")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register new customer", description = "Registers a new customer account.")
    @PostMapping("/customer/register")
    public ResponseEntity<?> registration(
            @RequestBody @Valid RegistrationRequest register,
            HttpServletRequest request
    ){
        authService.register(register, request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("register successfully.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @Operation(summary = "Admin login", description = "Authenticates an admin user and returns a JWT token.")
//    @PostMapping("/admin/login")
//    public ResponseEntity<?> adminLogin(
//            @RequestBody @Valid AdminLoginRequest login,
//            HttpServletRequest request
//    ){
//        LoginResponse loginResponse = authService.adminLogin(login, request);
//
//        ReturnObject response = ReturnObject.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.OK.value())
//                .message("login successfully.")
//                .data(loginResponse)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

//    @Operation(summary = "Customer login", description = "Authenticates a customer and returns a JWT token.")
//    @PostMapping("/customer/login")
//    public ResponseEntity<?> userLogin(
//            @RequestBody @Valid LoginRequest login,
//            HttpServletRequest request
//            ){
//        LoginResponse loginResponse = authService.userLogin(login, request);
//
//        ReturnObject response = ReturnObject.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.OK.value())
//                .message("login successfully.")
//                .data(loginResponse)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

}

package com.task.ecommerce.Auth;

import com.task.ecommerce.Auth.dto.AdminLoginRequest;
import com.task.ecommerce.Auth.dto.LoginRequest;
import com.task.ecommerce.Auth.dto.LoginResponse;
import com.task.ecommerce.Auth.dto.RegistrationRequest;
import com.task.ecommerce.utils.ReturnObject;
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
public class AuthController {
    private final AuthService authService;

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

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody @Valid AdminLoginRequest login,
            HttpServletRequest request
    ){
        LoginResponse loginResponse = authService.adminLogin(login, request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("login successfully.")
                .data(loginResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> userLogin(
            @RequestBody @Valid LoginRequest login,
            HttpServletRequest request
            ){
        LoginResponse loginResponse = authService.userLogin(login, request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("login successfully.")
                .data(loginResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

package com.task.ecommerce.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private String tokenType;

    private boolean enable;

    private boolean changePassword;
}
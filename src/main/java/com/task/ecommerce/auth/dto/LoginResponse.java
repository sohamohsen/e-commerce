package com.task.ecommerce.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private boolean enable;
    private boolean changePassword;
}

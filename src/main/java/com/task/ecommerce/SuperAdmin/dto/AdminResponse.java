package com.task.ecommerce.SuperAdmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Boolean enabled;
    private boolean accountLocked;
    private boolean passwordChanged;
    private LocalDateTime createdAt;
}
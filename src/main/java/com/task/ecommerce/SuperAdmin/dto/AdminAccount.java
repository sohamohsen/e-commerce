package com.task.ecommerce.SuperAdmin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminAccount {

    private String name;
    private String email;
    private String phone;
    private String password;
}

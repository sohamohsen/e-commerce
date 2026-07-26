package com.task.ecommerce.payment.dto;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String dateJoined;
    private String email;
    private Boolean isActive;
    private Boolean isStaff;
    private Boolean isSuperuser;
    private String lastLogin;
    private List<Object> groups;
    private List<Integer> userPermissions;
}
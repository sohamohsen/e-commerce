package com.task.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponse {

    private Integer id;
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer createdBy;
    private Integer updatedBy;

}

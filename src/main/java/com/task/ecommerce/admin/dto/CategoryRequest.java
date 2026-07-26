package com.task.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
    private String name;
}

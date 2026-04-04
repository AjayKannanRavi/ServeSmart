package com.servesmart.dto;

import lombok.Data;

@Data
public class MenuItemRequest {
    private String name;
    private String description;
    private Double price;
    private Boolean available;
    private Long categoryId;
    private String imageUrl;
}

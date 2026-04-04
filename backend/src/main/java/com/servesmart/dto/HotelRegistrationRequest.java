package com.servesmart.dto;

import com.servesmart.entity.Restaurant;
import lombok.Data;

@Data
public class HotelRegistrationRequest {
    private Restaurant restaurant;
    private String ownerEmail;
    private String adminUsername;
    private String adminPassword;
    private String kitchenUsername;
    private String kitchenPassword;
}

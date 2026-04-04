package com.servesmart.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String status;
    private String paymentMethod;
}

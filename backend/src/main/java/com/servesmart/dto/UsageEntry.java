package com.servesmart.dto;

import lombok.Data;

@Data
public class UsageEntry {
    private Long materialId;
    private Double usedQuantity;
}

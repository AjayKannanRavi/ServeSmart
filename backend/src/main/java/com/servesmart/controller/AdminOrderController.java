package com.servesmart.controller;

import com.servesmart.entity.RestaurantOrder;
import com.servesmart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<RestaurantOrder>> getAdminOrders(
            @RequestParam(value = "tableId", required = false) Long tableId) {
        Long restaurantId = com.servesmart.config.TenantContext.getCurrentTenantAsLong();
        if (tableId != null) {
            return ResponseEntity.ok(orderRepository.findByRestaurantTableId(tableId));
        }
        return ResponseEntity.ok(
                restaurantId != null ? orderRepository.findAllByRestaurantId(restaurantId) : orderRepository.findAll());
    }
}

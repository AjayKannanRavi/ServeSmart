package com.servesmart.service;

import com.servesmart.config.TenantContext;
import com.servesmart.entity.Restaurant;
import com.servesmart.entity.RestaurantTable;
import com.servesmart.repository.RestaurantRepository;
import com.servesmart.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {
    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public List<RestaurantTable> getAllTables() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null) {
            return java.util.Collections.emptyList();
        }
        return tableRepository.findByRestaurantId(restaurantId);
    }
    
    public RestaurantTable addTable(RestaurantTable table) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant not found in tenant database"));
        
        // Check for duplicate table number
        if (tableRepository.findByTableNumberAndRestaurantId(table.getTableNumber(), restaurantId).isPresent()) {
            throw new RuntimeException("Table number " + table.getTableNumber() + " already exists.");
        }
        
        // Enforce Subscription Limits
        if ("FREE".equalsIgnoreCase(restaurant.getPlanType())) {
            long currentCount = tableRepository.countByRestaurantId(restaurantId);
            if (currentCount >= 5) {
                throw new RuntimeException("FREE Plan limit reached: Maximum 5 tables allowed. Please upgrade to PREMIUM.");
            }
        }
        
        table.setRestaurant(restaurant);
        table.setQrCodeUrl("http://localhost:5173/login?hotelId=" + restaurantId + "&tableId=" + table.getTableNumber());
        if (table.getStatus() == null) table.setStatus("AVAILABLE");
        if (table.getQrGenerated() == null) table.setQrGenerated(false);
        
        return tableRepository.save(table);
    }

    public RestaurantTable updateTable(Long id, RestaurantTable updated) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        return tableRepository.findById(id).map(t -> {
            if (!t.getRestaurant().getId().equals(restaurantId)) {
                throw new RuntimeException("Unauthorized access to table");
            }
            if (updated.getTableNumber() != null) {
                t.setTableNumber(updated.getTableNumber());
                // Update QR URL if table number changes
                t.setQrCodeUrl("http://localhost:5173/login?hotelId=" + t.getRestaurant().getId() + "&tableId=" + updated.getTableNumber());
            }
            if (updated.getStatus() != null) t.setStatus(updated.getStatus());
            if (updated.getQrGenerated() != null) t.setQrGenerated(updated.getQrGenerated());
            return tableRepository.save(t);
        }).orElseThrow(() -> new RuntimeException("Table not found"));
    }

    public void generateQr(Long id) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        
        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Unauthorized access to generate QR for this table");
        }
        
        table.setQrGenerated(true);
        tableRepository.save(table);
    }
}

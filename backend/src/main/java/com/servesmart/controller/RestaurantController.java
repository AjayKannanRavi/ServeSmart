package com.servesmart.controller;

import com.servesmart.config.TenantContext;
import com.servesmart.entity.Restaurant;
import com.servesmart.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    @GetMapping
    public ResponseEntity<Restaurant> getRestaurant() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId != null) {
            return restaurantRepository.findById(restaurantId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        List<Restaurant> all = restaurantRepository.findAll();
        if (all.isEmpty()) {
            Restaurant r = new Restaurant();
            r.setName("My Restaurant");
            return ResponseEntity.ok(restaurantRepository.save(r));
        }
        return ResponseEntity.ok(all.get(0));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestBody Restaurant updated) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId != null && !restaurantId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Restaurant> existing = restaurantRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Restaurant r = existing.get();
        r.setName(updated.getName());
        r.setLogoUrl(updated.getLogoUrl());
        r.setAddress(updated.getAddress());
        r.setContactNumber(updated.getContactNumber());
        r.setGstNumber(updated.getGstNumber());
        if (updated.getTaxPercentage() != null) r.setTaxPercentage(updated.getTaxPercentage());
        if (updated.getServiceCharge() != null) r.setServiceCharge(updated.getServiceCharge());
        return ResponseEntity.ok(restaurantRepository.save(r));
    }
}

package com.servesmart.controller;

import com.servesmart.entity.RawMaterial;
import com.servesmart.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialRepository repository;
    private final com.servesmart.repository.RestaurantRepository restaurantRepository;

    @GetMapping
    public List<RawMaterial> getAll() {
        Long restaurantId = com.servesmart.config.TenantContext.getCurrentTenantAsLong();
        return restaurantId != null ? repository.findByRestaurantId(restaurantId) : repository.findAll();
    }

    @PostMapping
    public RawMaterial create(@RequestBody RawMaterial rawMaterial) {
        Long restaurantId = com.servesmart.config.TenantContext.getCurrentTenantAsLong();
        if (restaurantId != null) {
            restaurantRepository.findById(restaurantId).ifPresent(rawMaterial::setRestaurant);
        }
        return repository.save(rawMaterial);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterial> update(@PathVariable Long id, @RequestBody RawMaterial details) {
        Long restaurantId = com.servesmart.config.TenantContext.getCurrentTenantAsLong();
        return repository.findById(id)
                .map(existing -> {
                    if (restaurantId != null && !existing.getRestaurant().getId().equals(restaurantId)) {
                        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                                .<RawMaterial>build();
                    }
                    existing.setName(details.getName());
                    existing.setQuantity(details.getQuantity());
                    existing.setUnit(details.getUnit());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long restaurantId = com.servesmart.config.TenantContext.getCurrentTenantAsLong();
        return repository.findById(id)
                .map(existing -> {
                    if (restaurantId != null && !existing.getRestaurant().getId().equals(restaurantId)) {
                        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).<Void>build();
                    }
                    repository.delete(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

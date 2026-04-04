package com.servesmart.controller;

import com.servesmart.config.TenantContext;
import com.servesmart.entity.Restaurant;
import com.servesmart.entity.Staff;
import com.servesmart.repository.RestaurantRepository;
import com.servesmart.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffRepository staffRepository;
    private final RestaurantRepository restaurantRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return ResponseEntity.ok(restaurantId != null ? staffRepository.findByRestaurantId(restaurantId) : staffRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId != null) {
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant context not found"));
            staff.setRestaurant(restaurant);
        }
        if (staff.getPassword() != null) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }
        return ResponseEntity.ok(staffRepository.save(staff));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable("id") Long id, @RequestBody Staff updated) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return staffRepository.findById(id).map(staff -> {
            if (restaurantId != null && !staff.getRestaurant().getId().equals(restaurantId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Staff>build();
            }
            staff.setName(updated.getName());
            staff.setRole(updated.getRole());
            staff.setUsername(updated.getUsername());
            staff.setPhone(updated.getPhone());
            if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                staff.setPassword(passwordEncoder.encode(updated.getPassword()));
            }
            return ResponseEntity.ok(staffRepository.save(staff));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update-by-role/{role}")
    public ResponseEntity<Staff> updateStaffByRole(@PathVariable("role") String role, @RequestBody Map<String, String> payload) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        com.servesmart.entity.StaffRole staffRole = com.servesmart.entity.StaffRole.valueOf(role.toUpperCase());
        Optional<Staff> staffOpt = staffRepository.findAll().stream()
                .filter(s -> s.getRestaurant() != null && s.getRestaurant().getId().equals(restaurantId) && s.getRole() == staffRole)
                .findFirst();

        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            if (payload.containsKey("username")) staff.setUsername(payload.get("username"));
            if (payload.containsKey("password") && !payload.get("password").isEmpty()) {
                staff.setPassword(passwordEncoder.encode(payload.get("password")));
            }
            return ResponseEntity.ok(staffRepository.save(staff));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, @RequestHeader(value = "X-Hotel-Id", required = false) Long hotelId) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username and password are required"));
        }

        Long effectiveHotelId = hotelId;
        if (effectiveHotelId == null && credentials.get("hotelId") != null) {
            try {
                effectiveHotelId = Long.parseLong(credentials.get("hotelId"));
            } catch (NumberFormatException ignored) {
                // Keep null and fall back below
            }
        }

        Optional<Staff> staff;
        if (effectiveHotelId != null) {
            // Use restaurant-scoped lookup in master context for stable authentication.
            // Frontend should pass hotelId in body and avoid tenant header for login.
            staff = staffRepository.findByUsernameAndRestaurantId(username, effectiveHotelId);
        } else {
            staff = staffRepository.findAll().stream()
                    .filter(s -> s.getUsername().equals(username))
                    .findFirst();
        }

        if (staff.isPresent() && staff.get().getPassword() != null && passwordEncoder.matches(password, staff.get().getPassword())) {
            return ResponseEntity.ok(staff.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("id") Long id) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return staffRepository.findById(id).map(staff -> {
            if (restaurantId != null && !staff.getRestaurant().getId().equals(restaurantId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            }
            staffRepository.delete(staff);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

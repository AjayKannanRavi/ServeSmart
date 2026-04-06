package com.servesmart.controller;

import com.servesmart.config.TenantContext;
import com.servesmart.entity.Restaurant;
import com.servesmart.entity.Staff;
import com.servesmart.entity.StaffRole;
import com.servesmart.repository.RestaurantRepository;
import com.servesmart.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        List<Staff> staff = restaurantId != null
                ? staffRepository.findByRestaurantId(restaurantId)
                : staffRepository.findAll();
        return ResponseEntity.ok(staff);
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId != null) {
            Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
            if (restaurant.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            staff.setRestaurant(restaurant.get());
        }
        if (staff.getPassword() != null) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }
        return ResponseEntity.ok(staffRepository.save(staff));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable("id") Long id,
            @RequestBody Staff updated) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        Optional<Staff> staffOpt = staffRepository.findById(id);
        if (staffOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Staff staff = staffOpt.get();
        if (restaurantId != null && staff.getRestaurant() != null
                && !restaurantId.equals(staff.getRestaurant().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        staff.setName(updated.getName());
        staff.setRole(updated.getRole());
        staff.setUsername(updated.getUsername());
        staff.setPhone(updated.getPhone());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return ResponseEntity.ok(staffRepository.save(staff));
    }

    @PutMapping("/update-by-role/{role}")
    public ResponseEntity<Staff> updateStaffByRole(
            @PathVariable("role") String role,
            @RequestBody Map<String, String> payload) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        StaffRole staffRole = StaffRole.valueOf(role.toUpperCase());
        Optional<Staff> staffOpt = staffRepository.findAll().stream()
                .filter(s -> s.getRestaurant() != null
                        && restaurantId.equals(s.getRestaurant().getId())
                        && s.getRole() == staffRole)
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
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credentials,
            @RequestHeader(value = "X-Hotel-Id", required = false) Long hotelId) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username and password are required"));
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
            staff = staffRepository.findByUsernameAndRestaurantId(username, effectiveHotelId);
        } else {
            staff = staffRepository.findAll().stream()
                    .filter(s -> username.equals(s.getUsername()))
                    .findFirst();
        }

        if (staff.isPresent() && staff.get().getPassword() != null
                && passwordEncoder.matches(password, staff.get().getPassword())) {
            return ResponseEntity.ok(staff.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("id") Long id) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        Optional<Staff> staffOpt = staffRepository.findById(id);
        if (staffOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Staff staff = staffOpt.get();
        if (restaurantId != null && staff.getRestaurant() != null
                && !restaurantId.equals(staff.getRestaurant().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        staffRepository.delete(staff);
        return ResponseEntity.noContent().build();
    }
}

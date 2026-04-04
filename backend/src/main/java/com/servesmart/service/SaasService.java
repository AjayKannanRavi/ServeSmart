package com.servesmart.service;

import com.servesmart.entity.Restaurant;
import com.servesmart.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaasService {

    private final RestaurantRepository restaurantRepository;
    private final com.servesmart.repository.StaffRepository staffRepository;
    private final com.servesmart.repository.OrderRepository orderRepository;
    private final com.servesmart.repository.PaymentRepository paymentRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final com.servesmart.config.DatabaseProvisioner databaseProvisioner;

    public java.util.Map<String, Object> getPlatformStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalHotels", restaurantRepository.count());
        stats.put("activeHotels", restaurantRepository.countByIsActive(true));
        stats.put("totalOrders", orderRepository.count());
        Double revenue = paymentRepository.getTotalPlatformRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : 0.0);
        stats.put("systemStatus", "OPERATIONAL");
        return stats;
    }

    public java.util.Map<String, Object> getSystemSettings() {
        java.util.Map<String, Object> settings = new java.util.HashMap<>();
        settings.put("platformName", "Vitteno Technologies");
        settings.put("maintenanceMode", false);
        settings.put("freePlanLimit", 10);
        settings.put("premiumMonthlyPrice", 49.99);
        return settings;
    }

    public java.util.Map<String, Object> getHotelDashboardStats(Long hotelId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        Restaurant restaurant = getHotelById(hotelId);
        
        stats.put("hotelName", restaurant.getName());
        stats.put("planType", restaurant.getPlanType());
        stats.put("expiryDate", restaurant.getPlanExpiry());
        
        // Real-time usage
        LocalDateTime todayStart = LocalDateTime.now().with(java.time.LocalTime.MIN);
        stats.put("todayOrders", paymentRepository.getTotalOrdersSince(todayStart, hotelId));
        Double rev = paymentRepository.getTotalRevenueSince(todayStart, hotelId);
        stats.put("todayRevenue", rev != null ? rev : 0.0);
        
        // 30-day analytics
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30).with(java.time.LocalTime.MIN);
        LocalDateTime now = LocalDateTime.now();
        
        Double monthlyRevenue = orderRepository.getTotalRevenueBetween(thirtyDaysAgo, now, hotelId);
        Long monthlyOrders = orderRepository.getTotalOrdersBetween(thirtyDaysAgo, now, hotelId);
        Double avgOrderValue = orderRepository.getAverageOrderValueBetween(thirtyDaysAgo, now, hotelId);
        
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("dailyRevenue", rev != null ? rev : 0.0);
        summary.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0);
        summary.put("totalOrders", monthlyOrders != null ? monthlyOrders : 0L);
        summary.put("avgOrderValue", avgOrderValue != null ? avgOrderValue : 0.0);
        
        List<Object[]> statusResults = orderRepository.getOrderStatusCountsBetween(thirtyDaysAgo, now, hotelId);
        java.util.Map<String, Long> statusBreakdown = new java.util.HashMap<>();
        for (Object[] res : statusResults) {
            statusBreakdown.put(res[0].toString(), (Long) res[1]);
        }
        summary.put("orderStatusBreakdown", statusBreakdown);
        stats.put("summary", summary);
        
        // Top dishes by quantity (30-day period)
        List<com.servesmart.entity.RestaurantOrder> ordersInPeriod = orderRepository.findAllByRestaurantId(hotelId).stream()
            .filter(o -> o.getCreatedAt().isAfter(thirtyDaysAgo) && o.getCreatedAt().isBefore(now))
            .collect(java.util.stream.Collectors.toList());
        
        java.util.Map<String, Integer> dishQuantities = new java.util.HashMap<>();
        java.util.Map<String, Double> dishRevenues = new java.util.HashMap<>();
        
        for (com.servesmart.entity.RestaurantOrder order : ordersInPeriod) {
            for (com.servesmart.entity.OrderItem item : order.getItems()) {
                String dishName = item.getMenuItem() != null ? item.getMenuItem().getName() : "Unknown";
                dishQuantities.put(dishName, dishQuantities.getOrDefault(dishName, 0) + item.getQuantity());
                dishRevenues.put(dishName, dishRevenues.getOrDefault(dishName, 0.0) + (item.getPrice() * item.getQuantity()));
            }
        }
        
        List<java.util.Map<String, Object>> topDishes = dishQuantities.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .map(e -> {
                java.util.Map<String, Object> dish = new java.util.HashMap<>();
                dish.put("name", e.getKey());
                dish.put("quantity", e.getValue());
                dish.put("revenue", dishRevenues.get(e.getKey()));
                return dish;
            })
            .collect(java.util.stream.Collectors.toList());
        
        stats.put("topDishes", topDishes);
        
        // Staff management (for owner visibility)
        List<com.servesmart.entity.Staff> staffList = staffRepository.findByRestaurantId(hotelId);
        List<java.util.Map<String, String>> credentials = staffList.stream().map(s -> {
            java.util.Map<String, String> m = new java.util.HashMap<>();
            m.put("role", s.getRole().toString());
            m.put("username", s.getUsername());
            m.put("name", s.getName());
            return m;
        }).collect(java.util.stream.Collectors.toList());
        stats.put("staffCredentials", credentials);
        
        return stats;
    }

    public List<Restaurant> getAllHotels() {
        return restaurantRepository.findAll();
    }

    public Restaurant getHotelById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }

    @Transactional
    public com.servesmart.dto.HotelRegistrationResponse createHotel(com.servesmart.dto.HotelRegistrationRequest request) {
        Restaurant restaurant = request.getRestaurant();
        if (restaurant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant details are required");
        }
        if (request.getAdminPassword() == null || request.getAdminPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adminPassword is required");
        }
        if (request.getAdminUsername() == null || request.getAdminUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adminUsername is required");
        }
        if (request.getKitchenPassword() == null || request.getKitchenPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "kitchenPassword is required");
        }
        if (request.getKitchenUsername() == null || request.getKitchenUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "kitchenUsername is required");
        }
        if (restaurant.getPlanType() == null) {
            restaurant.setPlanType("STARTER");
        }
        restaurant.setIsActive(true);
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // DYNAMIC DATABASE PROVISIONING
        databaseProvisioner.createTenantDatabase(saved.getId().toString());
        databaseProvisioner.seedTenantRestaurant(saved);

        // Prefer restaurant owner email, then top-level request email, else fallback to generated username
        String ownerEmail = restaurant.getOwnerEmail() != null ? restaurant.getOwnerEmail() : request.getOwnerEmail();
        String ownerUsername = ownerEmail != null ? ownerEmail.toLowerCase().trim() : "owner_" + saved.getId();
        com.servesmart.entity.Staff admin = new com.servesmart.entity.Staff();
        admin.setName(saved.getOwnerName() != null ? saved.getOwnerName() : "Owner");
        admin.setUsername(ownerUsername);
        String ownerRawPassword = restaurant.getOwnerPassword() != null && !restaurant.getOwnerPassword().trim().isEmpty()
            ? restaurant.getOwnerPassword()
            : request.getAdminPassword();
        admin.setPassword(passwordEncoder.encode(ownerRawPassword));
        admin.setRole(com.servesmart.entity.StaffRole.OWNER);
        admin.setPhone(saved.getContactNumber() != null ? saved.getContactNumber() : "0000000000");
        admin.setRestaurant(saved);
        staffRepository.save(admin);

        // STAFF ACCOUNT 2: MANAGER (OPERATIONAL ADMIN) - Use provided username
        String managerUsername = request.getAdminUsername().trim();
        com.servesmart.entity.Staff manager = new com.servesmart.entity.Staff();
        manager.setName("Restaurant Manager");
        manager.setUsername(managerUsername);
        manager.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        manager.setRole(com.servesmart.entity.StaffRole.ADMIN);
        manager.setPhone(saved.getContactNumber() != null ? saved.getContactNumber() : "0000000000");
        manager.setRestaurant(saved);
        staffRepository.save(manager);

        // STAFF ACCOUNT 3: KITCHEN (CHEF) - Use provided username
        String kitchenUsername = request.getKitchenUsername().trim();
        com.servesmart.entity.Staff kitchen = new com.servesmart.entity.Staff();
        kitchen.setName("Kitchen Staff");
        kitchen.setUsername(kitchenUsername);
        kitchen.setPassword(passwordEncoder.encode(request.getKitchenPassword()));
        kitchen.setRole(com.servesmart.entity.StaffRole.KITCHEN);
        kitchen.setPhone(saved.getContactNumber() != null ? saved.getContactNumber() : "0000000000");
        kitchen.setRestaurant(saved);
        staffRepository.save(kitchen);
        
        // Return response with generated credentials
        java.util.Map<String, String> credentials = new java.util.HashMap<>();
        credentials.put("hotelId", saved.getId().toString());
        credentials.put("ownerUsername", ownerUsername);
        credentials.put("ownerPassword", ownerRawPassword);
        credentials.put("managerUsername", managerUsername);
        credentials.put("managerPassword", request.getAdminPassword());
        credentials.put("kitchenUsername", kitchenUsername);
        credentials.put("kitchenPassword", request.getKitchenPassword());
        
        return new com.servesmart.dto.HotelRegistrationResponse(
            saved.getId(),
            saved.getName(),
            credentials
        );
    }

    @Transactional
    public Restaurant updateHotelPlan(Long id, String planType, Integer months) {
        Restaurant hotel = getHotelById(id);
        hotel.setPlanType(planType);
        if ("PREMIUM".equalsIgnoreCase(planType) && months != null) {
            LocalDateTime now = hotel.getPlanExpiry() != null && hotel.getPlanExpiry().isAfter(LocalDateTime.now()) 
                ? hotel.getPlanExpiry() : LocalDateTime.now();
            hotel.setPlanExpiry(now.plusMonths(months));
        } else if ("FREE".equalsIgnoreCase(planType)) {
            hotel.setPlanExpiry(null);
        }
        return restaurantRepository.save(hotel);
    }

    @Transactional
    public Restaurant toggleHotelStatus(Long id) {
        Restaurant hotel = getHotelById(id);
        hotel.setIsActive(!hotel.getIsActive());
        return restaurantRepository.save(hotel);
    }
}

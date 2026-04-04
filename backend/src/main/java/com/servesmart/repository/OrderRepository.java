package com.servesmart.repository;

import com.servesmart.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<RestaurantOrder, Long> {
    @Query("SELECT o FROM RestaurantOrder o WHERE o.restaurantTable.id = :tableId")
    List<RestaurantOrder> findByRestaurantTableId(Long tableId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.restaurantTable.id = :tableId AND o.isActive = true")
    Optional<RestaurantOrder> findByRestaurantTableIdAndIsActiveTrue(Long tableId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.sessionId = :sessionId")
    List<RestaurantOrder> findBySessionId(String sessionId);

    @Query("SELECT o FROM RestaurantOrder o WHERE (:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId)")
    List<RestaurantOrder> findAllByRestaurantId(Long restaurantId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.isActive = :isActive AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    List<RestaurantOrder> findAllByIsActiveAndRestaurantId(boolean isActive, Long restaurantId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.sessionId = :sessionId AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    List<RestaurantOrder> findAllBySessionIdAndRestaurantId(String sessionId, Long restaurantId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.restaurantTable.tableNumber = :tableNumber AND o.isActive = :isActive AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    List<RestaurantOrder> findAllByTableAndIsActiveAndRestaurantId(Integer tableNumber, boolean isActive, Long restaurantId);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0.0) FROM `orders` WHERE payment_status = 'PAID' AND created_at >= :since AND (:restaurantId IS NULL AND restaurant_id IS NULL OR restaurant_id = :restaurantId)", nativeQuery = true)
    Double getTotalRevenueSince(LocalDateTime since, Long restaurantId);

    @Query(value = "SELECT COUNT(*) FROM `orders` WHERE payment_status = 'PAID' AND created_at >= :since AND (:restaurantId IS NULL AND restaurant_id IS NULL OR restaurant_id = :restaurantId)", nativeQuery = true)
    Long getTotalOrdersSince(LocalDateTime since, Long restaurantId);

    @Query(value = "SELECT DATE(created_at), SUM(total_amount) " +
           "FROM `orders` WHERE payment_status = 'PAID' AND (:restaurantId IS NULL AND restaurant_id IS NULL OR restaurant_id = :restaurantId) " +
           "GROUP BY DATE(created_at) " +
           "ORDER BY DATE(created_at) ASC", nativeQuery = true)
    List<Object[]> getDailyRevenueTrend(Long restaurantId);

    @Query("SELECT o.status, COUNT(o) FROM RestaurantOrder o WHERE (:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId) GROUP BY o.status")
    List<Object[]> getOrderStatusCounts(Long restaurantId);

    @Query(value = "SELECT HOUR(created_at) as hr, COUNT(*) as cnt FROM `orders` WHERE (:restaurantId IS NULL AND restaurant_id IS NULL OR restaurant_id = :restaurantId) GROUP BY hr ORDER BY hr", nativeQuery = true)
    List<Object[]> getOrdersByHour(Long restaurantId);

    @Query("SELECT AVG(o.totalAmount) FROM RestaurantOrder o WHERE o.paymentStatus = com.servesmart.entity.PaymentStatus.PAID AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    Double getAverageOrderValue(Long restaurantId);

    @Query("SELECT SUM(o.totalAmount) FROM RestaurantOrder o WHERE o.paymentStatus = com.servesmart.entity.PaymentStatus.PAID AND o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    Double getTotalRevenueBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);

    @Query("SELECT COUNT(o) FROM RestaurantOrder o WHERE o.status != com.servesmart.entity.OrderStatus.REJECTED AND o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    Long getTotalOrdersBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);

    @Query("SELECT CAST(o.createdAt AS date), SUM(o.totalAmount) FROM RestaurantOrder o WHERE o.paymentStatus = com.servesmart.entity.PaymentStatus.PAID AND o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId)) GROUP BY CAST(o.createdAt AS date) ORDER BY CAST(o.createdAt AS date) ASC")
    List<Object[]> getDailyRevenueTrendBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);

    @Query("SELECT o.status, COUNT(o) FROM RestaurantOrder o WHERE o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId)) GROUP BY o.status")
    List<Object[]> getOrderStatusCountsBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);

    @Query("SELECT HOUR(o.createdAt), COUNT(o) FROM RestaurantOrder o WHERE o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId)) GROUP BY HOUR(o.createdAt) ORDER BY HOUR(o.createdAt)")
    List<Object[]> getOrdersByHourBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);

    @Query("SELECT AVG(o.totalAmount) FROM RestaurantOrder o WHERE o.paymentStatus = com.servesmart.entity.PaymentStatus.PAID AND o.createdAt BETWEEN :start AND :end AND ((:restaurantId IS NULL AND o.restaurant IS NULL) OR (o.restaurant.id = :restaurantId))")
    Double getAverageOrderValueBetween(LocalDateTime start, LocalDateTime end, Long restaurantId);
}

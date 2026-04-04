package com.servesmart.repository;

import com.servesmart.entity.DailyUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyUsageLogRepository extends JpaRepository<DailyUsageLog, Long> {
    @Query("SELECT d FROM DailyUsageLog d WHERE (:restaurantId IS NULL AND d.restaurant IS NULL) OR (d.restaurant.id = :restaurantId)")
    List<DailyUsageLog> findByRestaurantId(Long restaurantId);

    @Query("SELECT d FROM DailyUsageLog d WHERE d.date BETWEEN :start AND :end AND ((:restaurantId IS NULL AND d.restaurant IS NULL) OR (d.restaurant.id = :restaurantId)) ORDER BY d.date DESC")
    List<DailyUsageLog> findByDateBetweenAndRestaurantIdOrderByDateDesc(LocalDateTime start, LocalDateTime end, Long restaurantId);
}

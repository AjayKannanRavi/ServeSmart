package com.servesmart.repository;

import com.servesmart.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    @Query("SELECT t FROM RestaurantTable t WHERE (:restaurantId IS NULL AND t.restaurant IS NULL) OR (t.restaurant.id = :restaurantId)")
    List<RestaurantTable> findByRestaurantId(Long restaurantId);

    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE (:restaurantId IS NULL AND t.restaurant IS NULL) OR (t.restaurant.id = :restaurantId)")
    long countByRestaurantId(Long restaurantId);

    @Query("SELECT t FROM RestaurantTable t WHERE t.tableNumber = :tableNumber AND ((:restaurantId IS NULL AND t.restaurant IS NULL) OR (t.restaurant.id = :restaurantId))")
    Optional<RestaurantTable> findByTableNumberAndRestaurantId(Integer tableNumber, Long restaurantId);
}

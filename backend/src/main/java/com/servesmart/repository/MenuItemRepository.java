package com.servesmart.repository;

import com.servesmart.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("SELECT m FROM MenuItem m WHERE (:restaurantId IS NULL AND m.restaurant IS NULL) OR (m.restaurant.id = :restaurantId)")
    List<MenuItem> findByRestaurantId(Long restaurantId);

    @Query("SELECT m FROM MenuItem m WHERE m.category.id = :categoryId AND ((:restaurantId IS NULL AND m.restaurant IS NULL) OR (m.restaurant.id = :restaurantId))")
    List<MenuItem> findByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId);
}

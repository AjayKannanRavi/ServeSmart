package com.servesmart.repository;

import com.servesmart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE (:restaurantId IS NULL AND c.restaurant IS NULL) OR (c.restaurant.id = :restaurantId)")
    List<Category> findByRestaurantId(Long restaurantId);
}

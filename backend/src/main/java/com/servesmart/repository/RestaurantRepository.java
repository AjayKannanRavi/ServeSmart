package com.servesmart.repository;

import com.servesmart.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    long countByIsActive(boolean isActive);
}

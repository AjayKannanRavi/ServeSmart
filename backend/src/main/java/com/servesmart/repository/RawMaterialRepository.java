package com.servesmart.repository;

import com.servesmart.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    @Query("SELECT r FROM RawMaterial r WHERE (:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId)")
    List<RawMaterial> findByRestaurantId(Long restaurantId);
}

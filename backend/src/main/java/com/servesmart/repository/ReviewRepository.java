package com.servesmart.repository;

import com.servesmart.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE (:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId)")
    List<Review> findByRestaurantId(Long restaurantId);

    @Query("SELECT r FROM Review r WHERE r.sessionId = :sessionId")
    Optional<Review> findBySessionId(String sessionId);

    @Query("SELECT r FROM Review r WHERE r.sessionId = :sessionId AND ((:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId))")
    Optional<Review> findBySessionIdAndRestaurantId(String sessionId, Long restaurantId);

    boolean existsBySessionId(String sessionId);

    @Query("SELECT AVG(cast(r.overallRating as double)) FROM Review r WHERE (:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId)")
    Double getAverageRating(Long restaurantId);

    @Query("SELECT r.overallRating, COUNT(r) FROM Review r WHERE (:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId) GROUP BY r.overallRating ORDER BY r.overallRating DESC")
    List<Object[]> getRatingDistribution(Long restaurantId);

    @Query("SELECT COUNT(r) FROM Review r WHERE (:restaurantId IS NULL AND r.restaurant IS NULL) OR (r.restaurant.id = :restaurantId)")
    Long countByRestaurantId(Long restaurantId);
}

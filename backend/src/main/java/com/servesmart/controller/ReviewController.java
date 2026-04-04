package com.servesmart.controller;

import com.servesmart.dto.ReviewRequest;
import com.servesmart.config.TenantContext;
import com.servesmart.entity.Review;
import com.servesmart.repository.RestaurantRepository;
import com.servesmart.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<Review> submitReview(@RequestBody ReviewRequest request) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (reviewRepository.findBySessionIdAndRestaurantId(request.getSessionId(), restaurantId).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Review review = new Review();
        review.setSessionId(request.getSessionId());
        review.setTableId(request.getTableId());
        review.setOverallRating(request.getOverallRating());
        review.setComment(request.getComment());
        review.setItemRatingsJson(request.getItemRatingsJson());
        
        if (restaurantId != null) {
            restaurantRepository.findById(restaurantId).ifPresent(review::setRestaurant);
        }
        
        Review saved = reviewRepository.save(review);
        
        // Broadcast for real-time admin updates
        if (restaurantId != null) {
            messagingTemplate.convertAndSend("/topic/" + restaurantId + "/reviews", saved);
        } else {
            messagingTemplate.convertAndSend("/topic/reviews", saved);
        }
        
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Review> getReview(@PathVariable String sessionId) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        Optional<Review> review = reviewRepository.findBySessionIdAndRestaurantId(sessionId, restaurantId);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<java.util.List<Review>> getAllReviews() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return ResponseEntity.ok(reviewRepository.findByRestaurantId(restaurantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return reviewRepository.findById(id).map(review -> {
            if (restaurantId != null && !review.getRestaurant().getId().equals(restaurantId)) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).<Void>build();
            }
            reviewRepository.delete(review);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

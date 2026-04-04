package com.servesmart.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "item_ratings_json", columnDefinition = "TEXT")
    private String itemRatingsJson; // JSON string of {menuItemId: rating}

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

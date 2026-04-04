package com.servesmart.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_usage_logs")
@Data
public class DailyUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private String materialName;

    @Column(nullable = false)
    private Double usedQuantity;

    @Column(nullable = false)
    private Double remainingQuantity;

    @Column(nullable = false)
    private String unit;
}

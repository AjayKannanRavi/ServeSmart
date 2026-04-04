package com.servesmart.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;

    @Column(name = "service_charge", nullable = false)
    private Double serviceCharge;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod; // CASH, CARD, ONLINE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}

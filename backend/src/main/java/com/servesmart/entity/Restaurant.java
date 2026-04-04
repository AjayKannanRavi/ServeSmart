package com.servesmart.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant")
@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "owner_password")
    private String ownerPassword;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "logo_url")
    private String logoUrl;

    private String address;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "plan_type", nullable = false)
    private String planType = "STARTER"; // STARTER, CLASSIC, or PREMIUM

    @Column(name = "plan_expiry")
    private LocalDateTime planExpiry;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "tax_percentage")
    private Double taxPercentage = 5.0;

    @Column(name = "service_charge")
    private Double serviceCharge = 0.0;


    public double getTaxPercentageSafe() {
        return taxPercentage != null ? taxPercentage : 5.0;
    }

    public double getServiceChargeSafe() {
        return serviceCharge != null ? serviceCharge : 0.0;
    }
}

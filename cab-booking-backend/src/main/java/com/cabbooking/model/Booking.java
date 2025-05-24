package com.cabbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        DRIVER_ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;

    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @Column(name = "dropoff_location", nullable = false)
    private String dropoffLocation;

    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    @Column(name = "dropoff_latitude")
    private Double dropoffLatitude;

    @Column(name = "dropoff_longitude")
    private Double dropoffLongitude;

    @Column(name = "distance", precision = 10, scale = 2)
    private BigDecimal distance; // in kilometers

    @Column(name = "fare", precision = 10, scale = 2)
    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "payment_status")
    private boolean paymentStatus = false;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "notes", length = 500)
    private String notes;

    // Helper methods
    public void startRide() {
        this.status = BookingStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void completeRide() {
        this.status = BookingStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void cancelRide() {
        if (this.status != BookingStatus.COMPLETED) {
            this.status = BookingStatus.CANCELLED;
            this.endTime = LocalDateTime.now();
        }
    }

    public boolean canBeCancelled() {
        return this.status == BookingStatus.PENDING 
            || this.status == BookingStatus.CONFIRMED 
            || this.status == BookingStatus.DRIVER_ASSIGNED;
    }
}

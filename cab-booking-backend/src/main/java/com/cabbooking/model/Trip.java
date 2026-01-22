package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "trips", indexes = {
        @Index(name = "idx_trip_passenger", columnList = "passenger_id"),
        @Index(name = "idx_trip_driver", columnList = "driver_id"),
        @Index(name = "idx_trip_status", columnList = "status")
})
public class Trip extends BaseEntity {

    public enum TripStatus {
        REQUESTED, ACCEPTED, ARRIVED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    @NotNull
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver; // Nullable until accepted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; // Snapshot of the car used

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TripStatus status = TripStatus.REQUESTED;

    // --- Location Data ---
    @NotNull
    @Column(name = "pickup_lat", precision = 10, scale = 7)
    private BigDecimal pickupLatitude;

    @NotNull
    @Column(name = "pickup_lng", precision = 10, scale = 7)
    private BigDecimal pickupLongitude;

    @NotBlank
    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;

    @NotNull
    @Column(name = "dropoff_lat", precision = 10, scale = 7)
    private BigDecimal dropoffLatitude;

    @NotNull
    @Column(name = "dropoff_lng", precision = 10, scale = 7)
    private BigDecimal dropoffLongitude;

    @NotBlank
    @Column(name = "dropoff_address", length = 500)
    private String dropoffAddress;

    // --- Financials ---
    @Column(name = "estimated_fare", precision = 19, scale = 4)
    private BigDecimal estimatedFare;

    @Column(name = "final_fare", precision = 19, scale = 4)
    private BigDecimal finalFare;

    @Column(name = "platform_fee", precision = 19, scale = 4)
    private BigDecimal platformFee;

    // --- Timestamps ---
    private LocalDateTime acceptedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String actualRoutePolyline;

    // --- Helper Logic (The Domain Logic) ---

    public void accept(Driver driver, Vehicle vehicle) {
        this.driver = driver;
        this.vehicle = vehicle;
        this.status = TripStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void start() {
        if (this.status != TripStatus.ARRIVED) {
            throw new IllegalStateException("Cannot start trip before driver has arrived.");
        }
        this.status = TripStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(BigDecimal finalFare) {
        this.status = TripStatus.COMPLETED;
        this.finalFare = finalFare;
        this.completedAt = LocalDateTime.now();
    }
}
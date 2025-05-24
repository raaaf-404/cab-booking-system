/**
 * Represents a cab/vehicle in the system.
 * Tracks location, status, and operational details.
 */

package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cabs", indexes = {
    @Index(name = "idx_cab_license_plate", columnList = "license_plate_number", unique = true),
    @Index(name = "idx_cab_location", columnList = "latitude,longitude"),
    @Index(name = "idx_cab_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)

public class Cab {

    public enum AvailabilityStatus {
        AVAILABLE,
        BOOKED,
        IN_RIDE,
        OFFLINE,
        MAINTENANCE
    }
    
    public enum VehicleType {
        SEDAN,
        SUV,
        VAN,
        LUXURY,
        TAXI
    }


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotBlank(message = "License plate is required")
@Size(max = 20, message = "License plate must be less than 20 characters")
@Column(name = "license_plate_number", nullable = false, unique = true)
private String licensePlateNumber;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "driver_id")
private User driver;


//Location
@Column(precision = 9, scale = 6)
private Double latitude;

@Column(precision = 9, scale = 6)
private Double longitude;

@Column(name = "last_location_update")
private LocalDateTime lastLocationUpdate;


//Status
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AvailabilityStatus status = AvailabilityStatus.OFFLINE;

@NotNull(message = "Vehicle type is required")
@Enumerated(EnumType.STRING)
@Column(name = "vehicle_type", nullable = false)
private VehicleType vehicleType;

//Pricing
@Column(name = "is_meter_fare", columnDefinition = "boolean default true")
private Boolean isMeterFare = false;

@Column(name = "base_fare", precision = 10, scale = 2)
private BigDecimal baseFare;

@Column(name = "rate_per_km", precision = 5, scale = 2)
private BigDecimal ratePerKm;

@Column(name = "model")
private String model;

@Column(name = "color")
private String color;

@Column(name = "year")
private Integer manufacturingYear;

@Min(value = 1, message = "Seating capacity must be at least 1")
@Max(value = 20, message = "Seating capacity cannot exceed 20")
@Column(name = "seating_capacity", nullable = false)
private Integer seatingCapacity = 4;  // Default value

@Column(name = "is_air_conditioned")
private Boolean isAirConditioned = false;

@CreationTimestamp
@Column(name = "created_at")
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;

public void markAvailable() {
    this.status = AvailabilityStatus.AVAILABLE;
    this.updatedAt = LocalDateTime.now();
}

public void markBooked() {
    this.status = AvailabilityStatus.BOOKED;
    this.updatedAt = LocalDateTime.now();
}

public BigDecimal calculateFare(Double distanceInKm) {
    if (!isMeterFare || baseFare == null || ratePerKm == null) {
        throw new IllegalStateException("Meter fare calculation not supported for this cab");
    }
    return baseFare.add(ratePerKm.multiply(BigDecimal.valueOf(distanceInKm)));
}

public void updateLocation(Double latitude, Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.lastLocationUpdate = LocalDateTime.now();
}

    
}

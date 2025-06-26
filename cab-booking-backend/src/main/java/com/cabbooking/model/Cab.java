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
import com.cabbooking.dto.request.CabUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Optional;

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
private Instant lastLocationUpdate;


//Status
@Builder.Default
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AvailabilityStatus status = AvailabilityStatus.OFFLINE;

@NotNull(message = "Vehicle type is required")
@Enumerated(EnumType.STRING)
@Column(name = "vehicle_type", nullable = false)
private VehicleType vehicleType;

//Pricing
@Builder.Default
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
@Builder.Default
private Integer seatingCapacity = 4;  // Default value

@Builder.Default
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
public BigDecimal calculateFare(BigDecimal distanceInKm) {
    if (!isMeterFare || baseFare == null || ratePerKm == null) {
        throw new IllegalStateException("Meter fare calculation not supported for this cab");
    }
    return baseFare.add(ratePerKm.multiply(distanceInKm));
}

public void updateLocation(Double latitude, Double longitude) {

    if (latitude == null || latitude < -90.0 || latitude > 90.0) {
        throw new IllegalArgumentException("Invalid latitude value: " + latitude);
    }
    if (longitude == null || longitude < -180.0 || longitude > 180.0) {
        throw new IllegalArgumentException("Invalid longitude value: " + longitude);
    }

    this.latitude = latitude;
    this.longitude = longitude;
    this.lastLocationUpdate = Instant.now();
}

public void updateFromRequest(CabUpdateRequest request, User driver) {
    Optional.ofNullable(request.getModel()).ifPresent(this::setModel);
    Optional.ofNullable(request.getColor()).ifPresent(this::setColor);
    Optional.ofNullable(request.getSeatingCapacity()).ifPresent(this::setSeatingCapacity);
    Optional.ofNullable(request.getIsAirConditioned()).ifPresent(this::setIsAirConditioned);
    Optional.ofNullable(request.getVehicleType()).ifPresent(type -> this.setVehicleType(Cab.VehicleType.valueOf(type)));
    Optional.ofNullable(request.getIsMeterFare()).ifPresent(this::setIsMeterFare);
    Optional.ofNullable(request.getBaseFare()).ifPresent(this::setBaseFare);
    Optional.ofNullable(request.getRatePerKm()).ifPresent(this::setRatePerKm);
    Optional.ofNullable(request.getStatus()).ifPresent(status -> this.setStatus(Cab.AvailabilityStatus.valueOf(status)));

    if (driver != null) {
        this.setDriver(driver);
    }
}

public void updateAvailabilityStatus(AvailabilityStatus newStatus) {
    this.status = newStatus;
    
}

}

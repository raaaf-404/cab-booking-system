package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers", indexes = {
        @Index(name = "idx_driver_status", columnList = "status"),
        @Index(name = "idx_driver_license", columnList = "license_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends BaseEntity {

    public enum DriverStatus {
        OFFLINE, ONLINE, ON_TRIP
    }

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    @Size(max = 50)
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DriverStatus status = DriverStatus.OFFLINE;

    // Last known snapshot of location
    @Column(name = "current_lat", precision = 10, scale = 7)
    private BigDecimal currentLat;

    @Column(name = "current_lng", precision = 10, scale = 7)
    private BigDecimal currentLng;

    @Min(1)
    @Max(5)
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "total_trips", nullable = false)
    @Builder.Default
    private Integer totalTrips = 0;

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    // --- Helper Methods ---

    /**
     * Updates the driver's location and heartbeat in one go.
     */
    public void updateLocation(BigDecimal lat, BigDecimal lng) {
        this.currentLat = lat;
        this.currentLng = lng;
        this.lastHeartbeat = LocalDateTime.now();
    }

    public void verify() {
        this.isVerified = true;
    }
}
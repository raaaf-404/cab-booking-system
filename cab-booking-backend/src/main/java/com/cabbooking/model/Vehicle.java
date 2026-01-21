package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_plate", columnList = "plate_number"),
        @Index(name = "idx_vehicle_driver", columnList = "driver_id")
})
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    @NotNull(message = "Vehicle must be assigned to a driver")
    private Driver driver;

    @NotBlank
    @Size(max = 20)
    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    @NotBlank
    @Column(nullable = false)
    private String model;

    @NotBlank
    @Column(nullable = false)
    private String color;

    @Min(2000)
    @Max(2026)
    @Column(nullable = false)
    private Integer year;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Builder
    public Vehicle(Long id, Driver driver, String plateNumber, String model,
                   String color, Integer year, Boolean isActive) {
        this.id = id;
        this.driver = driver;
        this.plateNumber = normalizePlate(plateNumber);
        this.model = model;
        this.color = color;
        this.year = year;
        this.isActive = isActive != null ? isActive : false;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = normalizePlate(plateNumber);
    }

    private String normalizePlate(String plate) {
        return plate != null ? plate.toUpperCase().replaceAll("\\s|-", "") : null;
    }
}
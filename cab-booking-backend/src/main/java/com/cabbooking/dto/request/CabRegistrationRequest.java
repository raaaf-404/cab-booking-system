package com.cabbooking.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CabRegistrationRequest {

    @NotBlank(message = "License plate number is required")
    @Size (max = 20, message = "License plate must be less than 20 characters")
    private String licensePlateNumber;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    private String model;

    private String color;

    private Integer manufacturingYear;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 20, message = "Seating capacity cannot exceed 20")
    private Integer seatingCapacity;

    @NotNull(message = "Air conditioned status is required") // Booleans should use @NotNull
    private Boolean isAirConditioned;

    @NotNull(message = "Meter fare status is required")
    private Boolean isMeterFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Base fare must be a non-negative value")
    @Digits(integer = 8, fraction = 2, message = "Base fare must have up to 8 digits before and 2 digits after the decimal point")
    private BigDecimal baseFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Rate per km must be a non-negative value")
    @Digits(integer = 3, fraction = 2, message = "Rate per km must have up to 3 digits before and 2 digits after the decimal point")
    private BigDecimal ratePerKm;

}
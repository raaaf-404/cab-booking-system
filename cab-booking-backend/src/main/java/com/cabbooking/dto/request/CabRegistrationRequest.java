package com.cabbooking.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CabRegistrationRequest {

    @NotBlank(message = "License plate number is required")
    @Size (max = 20, message = "License plate must be less than 20 characters")
    private String licensePlateNumber;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Vechicle tpe is required")
    private String vehicleType;

    private String model;

    private String color;

    private Integer manufacturingYear;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacty must be at least 1")
    @Max(value = 20, message = "Seating capacity cannot exceed 20")
    private Integer seatingCapacity;

    @NotBlank(message = "Air conditioned status is required")
    private Boolean isAirConditioned;

    @NotNull(message = "Meter fare status is required")
    private Boolean isMeterFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Base fare must be a non-nagative value")
    @Digits(integer = 8, fraction = 2, message = "Base fare must have up to 8 dgits before and 2 digits after the decimal point")
    private BigDecimal baseFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Rate per km nust be a non-negative value")
    @Digits(integer = 8, fraction = 2, message = "Rate per km must have  up to 9 digits before and 2 digits after the decimal point")
    private BigDecimal ratePerKm;


}
package com.cabbooking.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CabUpdateRequest {

    private String model;

    private String color;

    @Min(value = 1, message = "Seating  capacity must be at least 1")
    @Max(value = 20, message = "Seating capacity cannot exceed 20")
    private Integer seatingCapacity;

    private Boolean isAirConditioned;

    private String vehicleType;

    private Boolean isMeterFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Base fare must be a non-negative value")
    @Digits(integer = 8, fraction = 2, message = "Base fare must have up to 8 digits before and 2 digits after the decimal point")
    private BigDecimal baseFare;

    @DecimalMin(value = "0.00", inclusive = true, message = "Rate per km must be a non-negative value")
    @Digits(integer = 3, fraction = 2, message = "Rate per km must have up to 3 digits before and 2 digits after the decimal point")
    private BigDecimal ratePerKm;

    private Long driverId;

    private String status;
}

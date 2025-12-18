package com.cabbooking.dto.response;

import com.cabbooking.model.Cab;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.math.BigDecimal;

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.Cab.VehicleType;
import com.cabbooking.model.Cab.AvailabilityStatus;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CabResponse {
    private Long id;
    private String licensePlateNumber;
    private UserResponse driver;
    private Double latitude;
    private Double longitude; 
    private Instant lastLocationUpdate;
    private AvailabilityStatus status;
    private VehicleType vehicleType;
    private Boolean isMeterFare;
    private BigDecimal baseFare;
    private BigDecimal ratePerKm;
    private String model;
    private String color;
    private Integer manufacturingYear;
    private Integer seatingCapacity;
    private Boolean isAirConditioned;
}

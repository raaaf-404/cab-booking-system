package com.cabbooking.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.cabbooking.dto.response.UserResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabResponse {
    private Long id;
    private String licensePlateNumber;
    private UserResponse driver;
    private Double latitude;
    private Double longitude; 
    private LocalDateTime lastLocationUpdate;
    private String status;
    private String vehicleType;
    private Boolean isMeterFare;
    private BigDecimal baseFare;
    private BigDecimal ratePerKm;
    private String model;
    private String color;
    private Integer manufacturingYear;
    private Integer seatingCapacity;
    private Boolean isAirConditioned;
}

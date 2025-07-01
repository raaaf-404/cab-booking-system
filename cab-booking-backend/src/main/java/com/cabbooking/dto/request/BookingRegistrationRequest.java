package com.cabbooking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRegistrationRequest {

    @NotNull(message = "Passenger ID is required" )
    private Long passengerId;

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Dropoff location is required")
    private String dropoffLocation;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private Double dropoffLatitude;

    private Double dropoffLongitude;

    private LocalDateTime scheduledTime;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}

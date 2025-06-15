package com.cabbooking.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private UserResponse passenger; // Using UserResponse DTO
    private UserResponse driver;    // Using UserResponse DTO, can be null
    private String pickupLocation;
    private String dropoffLocation;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double dropoffLatitude;
    private Double dropoffLongitude;
    private BigDecimal distance;
    private BigDecimal fare;
    private String status; // e.g., "PENDING", "CONFIRMED" (from Booking.BookingStatus enum)
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean paymentStatus;
    private String paymentId;
    private String notes;
}

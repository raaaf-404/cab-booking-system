package com.cabbooking.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;;

@Data
public class DriverAssignmentRequest {
    @NotNull(message = "Driver ID cannot be null")
    private Long driverId;
}

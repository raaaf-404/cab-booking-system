package com.cabbooking.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import com.cabbooking.model.Cab;

/**
 * DTO for updating the availability status of a Cab.
 * Accepts the Cab enum status.
 */
@Data
public class CabUpdateAvailabilityStatusRequest {
    @NotBlank(message = "Availability status cannot be blank")
    private Cab.AvailabilityStatus status;
}

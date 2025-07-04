package com.cabbooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationUpdateRequest {

    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @Min(value = -180, message = "Latitude must be between -180 and 180")
    @Max(value = 180, message = "Latitude must be between -180 and 180")
    private Double longitude;
}

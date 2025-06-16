package com.cabbooking.service;

import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Cab.AvailabilityStatus;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.dto.request.CabUpdateRequest;

import java.util.List;
import java.util.Optional;


public interface CabService {
    
    CabResponse registerCab(CabRegistrationRequest registratrationRequest);
    Optional<CabResponse> getCarById(Long cabId);
    Optional<CabResponse> getCabByLicensePlate(String licensePlateNumber);
    CabResponse updateCabDetails(Long cabId, CabUpdateRequest request);
    CabResponse updateCabLocation(Long cabId, Double latitude, Double longitude);
    CabResponse updateCabAvailabilityStatus(Long cabId, AvailabilityStatus status);
    CabResponse assignDriverToCab(Long cabId, Long driverId);
    CabResponse removeDriverFromCab(Long cabId);
    List<CabResponse> findAvailableCabs(Cab.VehicleType vehicleType);
    List<CabResponse> getAllCabs();
    void deleteCab(Long cabId);
}

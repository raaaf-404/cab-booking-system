package com.cabbooking.service;

import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateAvailabilityStatusRequest;
import com.cabbooking.dto.request.LocationUpdateRequest;
import com.cabbooking.dto.request.DriverAssignmentRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Cab.AvailabilityStatus;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.dto.request.CabUpdateRequest;
import com.cabbooking.dto.request.DriverAssignmentRequest;

import java.util.List;


public interface CabService {
    
    CabResponse registerCab(CabRegistrationRequest registrationRequest);

    CabResponse getCabById(Long cabId);

    CabResponse getCabByLicensePlate(String licensePlateNumber);

    CabResponse updateCabDetails(Long cabId, CabUpdateRequest request);

    CabResponse updateCabLocation(Long cabId, LocationUpdateRequest request);

    CabResponse updateCabAvailabilityStatus(Long cabId, CabUpdateAvailabilityStatusRequest request);

    CabResponse assignDriverToCab(Long cabId, DriverAssignmentRequest request);

    CabResponse removeDriverFromCab(Long cabId);

    List<CabResponse> findAvailableCabs(Cab.VehicleType vehicleType);

    List<CabResponse> getAllCabs();
    
    void deleteCab(Long cabId);
}

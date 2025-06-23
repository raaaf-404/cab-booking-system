package com.cabbooking.service.impl;

import com.cabbooking.service.CabService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.mapper.UserMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CabServiceImpl implements CabService {
    private final CabRepository cabRepository;
    private final UserMapper userMapper;

    private CabResponse convertToCabResponse(Cab cab) {
        if (cab == null) {
            return null;
        }
        CabResponse response = new CabResponse();
        response.setId(cab.getId());
        response.setLicensePlateNumber(cab.getLicensePlateNumber());
        if (cab.getDriver() != null) {
            response.setDriver(UserMapper.userMapper(cab.getDriver()));
        }
        response.setLatitude(cab.getLatitude());
        response.setLongitude(cab.getLongitude());
        response.setLastLocationUpdate(cab.getLastLocationUpdate());
        if (cab.getStatus() != null) {
            response.setStatus(cab.getStatus().name());
        }
        if (cab.getVehicleType() != null) {
            response.setVehicleType(cab.getVehicleType().name());
        }
        response.setIsMeterFare(cab.getIsMeterFare());
        response.setBaseFare(cab.getBaseFare());
        response.setRatePerKm(cab.getRatePerKm());
        response.setModel(cab.getModel());
        response.setColor(cab.getColor());
        response.setManufacturingYear(cab.getManufacturingYear());
        response.setSeatingCapacity(cab.getSeatingCapacity());
        response.setIsAirConditioned(cab.getIsAirConditioned());
        return response;
    }
}

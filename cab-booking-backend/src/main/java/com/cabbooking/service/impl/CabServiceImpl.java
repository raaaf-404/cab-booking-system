package com.cabbooking.service.impl;

import com.cabbooking.service.CabService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.model.User;
import com.cabbooking.mapper.UserMapper;
import com.cabbooking.exception.CabAlreadyExistException;
import com.cabbooking.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CabServiceImpl implements CabService {
    private final CabRepository cabRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

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

    @Transactional
    @Override
    public CabResponse registerCab(CabRegistrationRequest request) {

        //Fetch Driver
        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + request.getDriverId()));

        //Validation
        if (!driver.getRole().contains(User.Role.DRIVER)) {
            throw new IllegalArgumentException("User with id " + request.getDriverId() + " is not a DRIVER.");
        }

        if (cabRepository.findByLicensePlateNumber(request.getLicensePlateNumber()).isPresent()) {
            throw new CabAlreadyExistException("Cab with license plate numebr " + request.getLicensePlateNumber() + " already exists.");
        }
        
        //Register Cab
        Cab cab = new Cab();
        cab.setDriver(driver);
        cab.setLicensePlateNumber(request.getLicensePlateNumber());
        cab.setVehicleType(Cab.VehicleType.valueOf(request.getVehicleType()));
        cab.setIsMeterFare(request.getIsMeterFare());
        cab.setBaseFare(request.getBaseFare());
        cab.setRatePerKm(request.getRatePerKm());
        cab.setModel(request.getModel());
        cab.setColor(request.getColor());
        cab.setManufacturingYear(request.getManufacturingYear());
        cab.setSeatingCapacity(request.getSeatingCapacity());
        cab.setIsAirConditioned(request.getIsAirConditioned());
        return convertToCabResponse(cabRepository.save(cab));
    }
}

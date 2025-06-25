package com.cabbooking.service.impl;

import com.cabbooking.service.CabService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.model.User;
import com.cabbooking.mapper.CabMapper;
import com.cabbooking.exception.CabAlreadyExistException;
import com.cabbooking.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CabServiceImpl implements CabService {
    private final CabRepository cabRepository;
    private final CabMapper cabMapper;
    private final UserRepository userRepository;

    private User validateAndGetDriver(Long driverId) {

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        if (!driver.getRole().contains(User.Role.DRIVER)) {
            throw new IllegalArgumentException("User with id " + driverId + " is not a DRIVER.");
        }

        return driver;
        }

    @Transactional
    @Override
    public CabResponse registerCab(CabRegistrationRequest request) {
        User driver = validateAndGetDriver(request.getDriverId());
        
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

        Cab savedCab = cabRepository.save(cab);

        return cabMapper.toCabResponse(savedCab);
    }
    
    @Transactional(readOnly = true)
    @Override
    public CabResponse getCabById(Long id) {
        return cabRepository.findById(id)
                .map(cabMapper::toCabResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + id));
    }
}

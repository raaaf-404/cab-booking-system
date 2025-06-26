package com.cabbooking.service.impl;

import com.cabbooking.service.CabService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateRequest;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.model.User;
import com.cabbooking.mapper.CabMapper;
import com.cabbooking.exception.CabAlreadyExistException;
import com.cabbooking.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CabServiceImpl implements CabService {
    private final CabRepository cabRepository;
    private final CabMapper cabMapper;
    private final UserRepository userRepository;

    private User validateAndGetDriverById(Long driverId) {

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
        User driver = validateAndGetDriverById(request.getDriverId());
        
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

    @Transactional(readOnly = true)
    @Override
    public CabResponse getCabByLicensePlate(String licensePlateNumber) {
        return cabRepository.findByLicensePlateNumber(licensePlateNumber)
                .map(cabMapper::toCabResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with license plate number: " + licensePlateNumber));
    }

    @Transactional
    @Override
    public CabResponse updateCabDetails(Long cabId, CabUpdateRequest request) {
       Cab cab = cabRepository.findById(cabId)
            .orElseThrow(() -> new ResourceNotFoundException("Cab not found with an id: " + cabId));

      User driverToSet = Optional.ofNullable(request.getDriverId())
            .map(this::validateAndGetDriverById)
            .orElse(null);

      cab.updateFromRequest(request, driverToSet);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse updateCabLocation(Long cabId, Double latitude, Double longitude) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));
        cab.updateLocation(latitude, longitude);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse updateCabAvailabilityStatus(Long cabId, Cab.AvailabilityStatus status) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));

        cab.updateAvailabilityStatus(status);
        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse assignDriverToCab(Long cabId, Long driverId) {

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));

        User driver = validateAndGetDriverById(driverId);

        if (cab.getDriver() != null) {
            throw new IllegalStateException("Cab is already assigned to a driver.");
        }

        cabRepository.findByDriver(driver).ifPresent(existingCab -> {
            throw new IllegalStateException("Driver with id " + driverId + " is already assigned to another cab with id " + existingCab.getId());
        });

        cab.setDriver(driver);
        cab.setStatus(Cab.AvailabilityStatus.AVAILABLE);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse removeDriverFromCab(Long cabId) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId);

        if (cab.getDriver() == null) {
            throw new IllegalStateException("Cab with id " + cabId + " has no driver assigned.");
        }

        cab.setDriver(null);
        cab.setStatus(Cab.AvailabilityStatus.OFFLINE);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }


  
}
package com.cabbooking.service.impl;

import com.cabbooking.service.CabService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateRequest;
import com.cabbooking.dto.request.DriverAssignmentRequest;
import com.cabbooking.dto.request.CabUpdateAvailabilityStatusRequest;
import com.cabbooking.dto.request.LocationUpdateRequest;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.model.User;
import com.cabbooking.mapper.CabMapper;
import com.cabbooking.exception.CabAlreadyExistException;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.service.UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CabServiceImpl implements CabService {
    private final CabRepository cabRepository;
    private final CabMapper cabMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public CabResponse registerCab(CabRegistrationRequest request) {
        User driver = userService.findAndValidateDriverById(request.getDriverId());
        
        if (cabRepository.findByLicensePlateNumber(request.getLicensePlateNumber()).isPresent()) {
            throw new CabAlreadyExistException("Cab with license plate number " + request.getLicensePlateNumber() + " already exists.");
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
            .map(userService::findAndValidateDriverById)
            .orElse(null);

      cab.updateFromRequest(request, driverToSet);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse updateCabLocation(Long cabId, LocationUpdateRequest request) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));
                
        cab.updateLocation(request.getLatitude(), request.getLongitude());

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse updateCabAvailabilityStatus(Long cabId, CabUpdateAvailabilityStatusRequest request) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));

        cab.updateAvailabilityStatus(request.getStatus());
        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }

    @Transactional
    @Override
    public CabResponse assignDriverToCab(Long cabId, DriverAssignmentRequest request) {

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));

        Long driverId = request.getDriverId();
        User driver = userService.findAndValidateDriverById(driverId);

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
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with id: " + cabId));

        if (cab.getDriver() == null) {
            throw new IllegalStateException("Cab with id " + cabId + " has no driver assigned.");
        }

        cab.setDriver(null);
        cab.setStatus(Cab.AvailabilityStatus.OFFLINE);

        Cab updatedCab = cabRepository.save(cab);
        return cabMapper.toCabResponse(updatedCab);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<CabResponse> findAvailableCabs(Cab.VehicleType vehicleType) {

        List<Cab> availableCabs;
        if (vehicleType != null) {
            availableCabs =  cabRepository.findByVehicleTypeAndStatus(vehicleType, Cab.AvailabilityStatus.AVAILABLE);
        } else {
            availableCabs =  cabRepository.findByStatus(Cab.AvailabilityStatus.AVAILABLE);        
        }

        return availableCabs.stream()
                .map(cabMapper::toCabResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CabResponse> getAllCabs(Pageable pageable) {
        Page<Cab> cabsPage = cabRepository.findAll(pageable);
        return cabsPage.map(cabMapper::toCabResponse);
    }

    @Transactional
    @Override
    public void deleteCab(Long cabId) {
        
        Cab cab  = cabRepository.findById(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found with an id: " + cabId));

        cab.validateForDeletion();

        if (bookingRepository.existsByCab(cab)) {
            throw new IllegalStateException("Cannot delete cab with id " + cabId + "as it has associated booking records.");
        }

        cabRepository.delete(cab);
    }


  
}
    
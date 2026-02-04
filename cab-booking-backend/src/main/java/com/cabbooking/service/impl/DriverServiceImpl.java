package com.cabbooking.service.impl;

import com.cabbooking.dto.request.DriverSignupRequest;
import com.cabbooking.exception.DuplicateResourceException;
import com.cabbooking.model.Driver;
import com.cabbooking.model.User;
import com.cabbooking.model.enums.DriverStatus;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    @Transactional
    public Driver registerDriver(User user, DriverSignupRequest request) {
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new DuplicateResourceException("License number already registered");
        }

        Driver driver = Driver.builder()
                .user(user)
                .licenseNumber(request.licenseNumber())
                .isVerified(false) // Needs admin approval
                .status(DriverStatus.OFFLINE)
                .build();

        return driverRepository.save(driver);
    }
}
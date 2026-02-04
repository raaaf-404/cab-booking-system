package com.cabbooking.repository;

import com.cabbooking.model.Driver;
import com.cabbooking.model.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    /**
     * Find all drivers currently online and verified.
     * Useful for the matching engine.
     */
    List<Driver> findByStatusAndIsVerifiedTrue(DriverStatus status);

    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Driver> findByUserEmail(String email);
}
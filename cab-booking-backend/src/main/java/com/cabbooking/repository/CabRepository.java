package com.cabbooking.repository;

import com.cabbooking.model.Cab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CabRepository extends  JpaRepository<Cab, Long> {
    Optional<Cab> findByLicensePlateNumber(String licensePlateNumber);
    List<Cab> findByStatus(Cab.AvailabilityStatus status);
    List<Cab> findByVehicleTypeAndStatus(Cab.VehicleType vehicleType, Cab.AvailabilityStatus status);
    
}

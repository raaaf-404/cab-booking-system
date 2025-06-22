package com.cabbooking.repository;

import com.cabbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPassengerId(Long passengerId);
    List<Booking> findByDriverId(Long driverId);
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByStatusAndDriverIsNull(Booking.BookingStatus status);
    List<Booking> findByStatusInAndDriverIsNull(List<Booking.BookingStatus> statuses);
}

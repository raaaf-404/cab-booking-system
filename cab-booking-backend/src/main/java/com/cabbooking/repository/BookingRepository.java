package com.cabbooking.repository;

import com.cabbooking.model.Booking;
import com.cabbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

List<Booking> findByPassenger(User passenger);
List<Booking> findByDriver(User driver);
List<Booking> findByStatus(Booking.BookingStatus status);

}
